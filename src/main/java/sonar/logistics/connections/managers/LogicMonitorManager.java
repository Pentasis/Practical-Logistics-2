package sonar.logistics.connections.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.utils.Pair;
import sonar.logistics.Logistics;
import sonar.logistics.api.cache.ILogisticsNetwork;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.info.IInfoContainer;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.network.PacketInfoList;

public class LogicMonitorManager {

	public static final int UPDATE_RADIUS = 64;

	// server side
	public static final ArrayList<ILogicMonitor> monitors = new ArrayList();
	public static final ArrayList<IInfoDisplay> displays = new ArrayList();
	// client side
	public static LinkedHashMap<ILogicMonitor, MonitoredList<?>> monitoredLists = new LinkedHashMap();
	// both
	public static ArrayList<InfoUUID> changedInfo = new ArrayList();

	public static LinkedHashMap<InfoUUID, IMonitorInfo> lastInfo = new LinkedHashMap();
	public static LinkedHashMap<InfoUUID, IMonitorInfo> info = new LinkedHashMap();

	public static void onServerClosed() {
		monitors.clear();
		displays.clear();
		monitoredLists.clear();
		changedInfo.clear();
		lastInfo.clear();
		info.clear();
	}

	public static boolean enableEvents() {
		return !displays.isEmpty();
	}

	public static void addMonitor(ILogicMonitor monitor) {
		if (monitor.getCoords().getWorld().isRemote) {
			return;
		}
		if (monitors.contains(monitor)) {
			return;
		}
		monitors.add(monitor);
	}

	public static void addDisplay(IInfoDisplay display) {
		if (display.getCoords().getWorld().isRemote) {
			return;
		}
		if (displays.contains(display)) {
			return;
		}
		displays.add(display);
		onChunkAdded(new StoredChunkPos(display.getCoords()));
	}

	public static boolean removeMonitor(ILogicMonitor monitor) {
		if (monitor.getCoords().getWorld().isRemote) {
			return true;
		}
		if (!monitor.getNetwork().isFakeNetwork() && monitor.getNetwork() instanceof ILogisticsNetwork) {
			((ILogisticsNetwork) monitor.getNetwork()).removeMonitor(monitor);
		}
		return monitors.remove(monitor);
	}

	public static boolean removeDisplay(IInfoDisplay display) {
		if (display.getCoords().getWorld().isRemote) {
			return true;
		}
		onChunkRemoved(new StoredChunkPos(display.getCoords()));
		return displays.remove(display);
	}

	public static void onChunkAdded(StoredChunkPos pos) {
		/* monitoredChunks.putIfAbsent(pos.dim, new ArrayList()); for (StoredChunkPos chunk : monitoredChunks.get(pos.dim)) { if (chunk.equals(pos)) { chunk.addMonitor(); return; } } pos.addMonitor(); monitoredChunks.get(pos.dim).add(pos); */
	}

	public static void onChunkRemoved(StoredChunkPos pos) {
		/* if (monitoredChunks.get(pos.dim).contains(pos)) { for (StoredChunkPos chunk : monitoredChunks.get(pos.dim)) { if (chunk.equals(pos)) { if (chunk.removeMonitor() <= 0) { monitoredChunks.get(pos.dim).remove(chunk); } return; } } } */
	}

	public static ArrayList<IInfoDisplay> getViewableDisplays(EntityPlayer player) {
		ArrayList<IInfoDisplay> viewable = new ArrayList();
		World world = player.getEntityWorld();
		PlayerChunkMap manager = ((WorldServer) world).getPlayerChunkMap();
		for (IInfoDisplay display : displays) {
			if (manager.isPlayerWatchingChunk((EntityPlayerMP) player, display.getCoords().getX() >> 4, display.getCoords().getZ() >> 4)) {
				viewable.add(display);
			}
		}
		return viewable;
	}

	// server methods
	public static ILogicMonitor getMonitorFromClient(int hashCode) {
		for (ILogicMonitor monitor : monitors) {
			if (monitor.getIdentity().hashCode() == hashCode) {
				return monitor;
			}
		}
		return null;
	}

	public static void sendFullPacket(EntityPlayer player) {
		if (player != null) {
			ArrayList<IMonitorInfo> infoList = getInfoFromUUIDs(getUUIDsToSync(getViewableDisplays(player)));
			if (infoList.isEmpty()) {
				return;
			}
			NBTTagList packetList = new NBTTagList();
			for (IMonitorInfo info : infoList) {
				if (info != null && info.isValid() && !info.isHeader()) {
					packetList.appendTag(InfoHelper.writeInfoToNBT(new NBTTagCompound(), info, SyncType.SAVE));
				}
			}
			if (!packetList.hasNoTags()) {
				NBTTagCompound packetTag = new NBTTagCompound();
				packetTag.setTag("infoList", packetList);
				Logistics.network.sendTo(new PacketInfoList(packetTag, SyncType.SAVE), (EntityPlayerMP) player);
			}
		}
	}

	public static ArrayList<IMonitorInfo> getInfoFromUUIDs(ArrayList<InfoUUID> ids) {
		ArrayList<IMonitorInfo> infoList = new ArrayList();
		for (InfoUUID id : ids) {
			ILogicMonitor monitor = getMonitorFromClient(id.hashCode);
			if (monitor != null) {
				IMonitorInfo info = monitor.getMonitorInfo(id.pos);
				if (info != null) {
					infoList.add(info);
				}
			}
		}
		return infoList;
	}

	public static ArrayList<InfoUUID> getUUIDsToSync(ArrayList<IInfoDisplay> displays) {
		ArrayList<InfoUUID> ids = new ArrayList();
		for (IInfoDisplay display : displays) {
			IInfoContainer container = display.container();
			for (int i = 0; i < container.getMaxCapacity(); i++) {
				InfoUUID id = container.getInfoUUID(i);
				if (id.valid() && !ids.contains(id)) {
					ids.add(id);
				}
			}
		}
		return ids;
	}

	public IMonitorInfo getInfoFromUUID(InfoUUID uuid) {
		return info.get(uuid);
	}

	public static TargetPoint getTargetPointFromPlayer(EntityPlayer player) {
		return new TargetPoint(player.getEntityWorld().provider.getDimension(), player.posX, player.posY, player.posZ, UPDATE_RADIUS);
	}

	// client methods
	public static Pair<ILogicMonitor, MonitoredList<?>> getMonitorFromServer(int hashCode) {
		for (Entry<ILogicMonitor, ?> entry : monitoredLists.entrySet()) {
			if (entry.getKey().getIdentity().hashCode() == hashCode) {
				return new Pair(entry.getKey(), entry.getValue());
			}
		}
		return null;
	}

	public static <T extends IMonitorInfo> MonitoredList<T> getMonitoredList(ILogicMonitor monitor) {
		monitoredLists.putIfAbsent(monitor, MonitoredList.<T>newMonitoredList(monitor.getNetworkID()));
		for (Entry<ILogicMonitor, MonitoredList<?>> entry : monitoredLists.entrySet()) {
			 if (entry.getValue().networkID == entry.getKey().getNetworkID() && entry.getKey().getIdentity().equals(monitor.getIdentity())) {
				return (MonitoredList<T>) entry.getValue();
			}
		}
		return MonitoredList.<T>newMonitoredList(monitor.getNetworkID());
	}

	public static void onServerTick() {
		if (!changedInfo.isEmpty() && !displays.isEmpty()) {
			ArrayList<InfoUUID> infoToSync = (ArrayList<InfoUUID>) changedInfo.clone();
			LinkedHashMap<World, LinkedHashMap<ChunkPos, ArrayList<InfoUUID>>> positions = new LinkedHashMap();
			for (InfoUUID id : changedInfo) {
				boolean isSynced = false;
				if (id.valid()) {
					for (IInfoDisplay display : displays) {
						if (display.monitorsUUID(id)) {
							World world = display.getCoords().getWorld();
							positions.putIfAbsent(world, new LinkedHashMap());
							ChunkPos pos = new ChunkPos(display.getCoords().getBlockPos());
							positions.get(world).putIfAbsent(pos, new ArrayList());
							if (!positions.get(world).get(pos).contains(id)) {
								positions.get(world).get(pos).add(id);
							}
							isSynced = true;
						}
					}
				}
				if (!isSynced)
					infoToSync.remove(id);
			}
			changedInfo.clear();
			if (infoToSync.isEmpty()) {
				return;
			}
			Map<EntityPlayer, ArrayList<InfoUUID>> monitoring = new HashMap();
			// FIXME - THIS HAS LARGE OVERHEAD
			for (Entry<World, LinkedHashMap<ChunkPos, ArrayList<InfoUUID>>> chunks : positions.entrySet()) {
				World world = chunks.getKey();
				PlayerChunkMap manager = ((WorldServer) world).getPlayerChunkMap();
				for (EntityPlayer player : world.playerEntities) {
					NBTTagList packetList = new NBTTagList();
					for (Entry<ChunkPos, ArrayList<InfoUUID>> chunkInfo : chunks.getValue().entrySet()) {
						ChunkPos pos = chunkInfo.getKey();
						if (manager.isPlayerWatchingChunk((EntityPlayerMP) player, pos.chunkXPos, pos.chunkZPos)) {
							for (InfoUUID info : chunkInfo.getValue()) {
								monitoring.putIfAbsent(player, new ArrayList());
								if (!monitoring.get(player).contains(info)) {
									monitoring.get(player).add(info);
									IMonitorInfo monitorInfo = LogicMonitorManager.info.get(info);
									if (!(monitorInfo == null)) {
										// NBTTagCompound updateTag = monitorInfo.writeData(new NBTTagCompound(), SyncType.DEFAULT_SYNC);
										NBTTagCompound updateTag = InfoHelper.writeInfoToNBT(new NBTTagCompound(), monitorInfo, SyncType.SAVE);
										if (!updateTag.hasNoTags()) {
											packetList.appendTag(info.writeData(updateTag, SyncType.SAVE));
										}
									}
								}
							}
						}
					}
					if (!packetList.hasNoTags()) {
						NBTTagCompound packetTag = new NBTTagCompound();
						packetTag.setTag("infoList", packetList);
						Logistics.network.sendTo(new PacketInfoList(packetTag, SyncType.DEFAULT_SYNC), (EntityPlayerMP) player);
					}

				}
			}
		}
	}

	public static void onInfoPacket(NBTTagCompound packetTag, SyncType type) {
		NBTTagList packetList = packetTag.getTagList("infoList", NBT.TAG_COMPOUND);
		boolean save = type.isType(SyncType.SAVE);
		for (int i = 0; i < packetList.tagCount(); i++) {
			NBTTagCompound infoTag = packetList.getCompoundTagAt(i);
			InfoUUID id = NBTHelper.instanceNBTSyncable(InfoUUID.class, infoTag);
			if (!save) {
				LogicMonitorManager.info.replace(id, InfoHelper.readInfoFromNBT(infoTag));
			} else {
				LogicMonitorManager.info.put(id, InfoHelper.readInfoFromNBT(infoTag));
			}
		}
	}

	public static void changeInfo(InfoUUID id, IMonitorInfo newInfo) {
		LogicMonitorManager.lastInfo.put(id, LogicMonitorManager.info.get(id));
		LogicMonitorManager.info.put(id, newInfo);
		changedInfo.add(id);
	}

	public static class StoredChunkPos extends ChunkPos {

		// these won't be included in the hashCode
		public int monitorCount = 0;
		public int dim;

		public StoredChunkPos(int dim, BlockPos pos) {
			super(pos);
			this.dim = dim;
		}

		public StoredChunkPos(BlockCoords coords) {
			this(coords.getDimension(), coords.getBlockPos());
		}

		public int addMonitor() {
			return monitorCount++;
		}

		public int removeMonitor() {
			return monitorCount--;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (!(obj instanceof StoredChunkPos)) {
				return false;
			} else {
				StoredChunkPos chunkpos = (StoredChunkPos) obj;
				return this.chunkXPos == chunkpos.chunkXPos && this.chunkZPos == chunkpos.chunkZPos && dim == chunkpos.dim;
			}
		}

	}
}
