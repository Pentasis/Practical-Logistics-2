package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.List;

import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.PartSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.utils.BlockCoords;
import sonar.core.utils.Pair;
import sonar.core.utils.SonarValidation;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.EmptyNetworkCache;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.IDataEmitter;
import sonar.logistics.api.connecting.IDataReceiver;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.wrappers.CablingWrapper;
import sonar.logistics.common.multiparts.DataCablePart;
import sonar.logistics.common.multiparts.LogisticsMultipart;
import sonar.logistics.connections.managers.CableManager;
import sonar.logistics.connections.managers.NetworkManager;

public class CableHelper extends CablingWrapper {

	public IDataCable getCableFromCoords(BlockCoords coords) {
		IMultipartContainer container = MultipartHelper.getPartContainer(coords.getWorld(), coords.getBlockPos());
		if (container != null) {
			ISlottedPart part = container.getPartInSlot(PartSlot.CENTER);
			if (part != null && part instanceof IDataCable) {
				return (IDataCable) part;
			}
		}
		return null;
	}

	public ILogicTile getMultipart(BlockCoords coords, EnumFacing face) {
		IMultipartContainer container = MultipartHelper.getPartContainer(coords.getWorld(), coords.getBlockPos());
		if (container != null) {
			ISlottedPart part = container.getPartInSlot(PartSlot.getFaceSlot(face));
			if (part instanceof ILogicTile) {
				return (ILogicTile) part;
			}
		}
		return null;
	}

	public IInfoDisplay getDisplayScreen(BlockCoords coords, EnumFacing face) {
		IMultipartContainer container = MultipartHelper.getPartContainer(coords.getWorld(), coords.getBlockPos());
		if (container != null) {
			return getDisplayScreen(container, face);
		}
		return null;
	}

	public IInfoDisplay getDisplayScreen(IMultipartContainer container, EnumFacing face) {
		for (IMultipart part : container.getParts()) {
			if (part != null && part instanceof IInfoDisplay) {
				IInfoDisplay display = (IInfoDisplay) part;
				if (display.getFace() == face) {
					return display;
				}
			}
		}
		return null;
	}
	
	public static ArrayList<ILogicTile> getConnectedTiles(DataCablePart cable) {		
		return getConnectedTiles(cable, new SonarValidation.CLASS(ILogicTile.class));
	}

	public static <T> ArrayList<T> getConnectedTiles(DataCablePart cable, Class<T> type) {		
		return getConnectedTiles(cable, new SonarValidation.CLASS(type));
	}

	public static <T> ArrayList<T> getConnectedTiles(DataCablePart cable, SonarValidation validate) {
		ArrayList<T> logicTiles = new ArrayList();
		for (IMultipart part : cable.getContainer().getParts()) {
			if (validate.isValid(part)) {
				logicTiles.add((T) part);
			}
		}
		for (EnumFacing face : EnumFacing.values()) {
			BlockCoords offset = BlockCoords.translateCoords(cable.getCoords(), face.getOpposite());
			ILogicTile tile = LogisticsAPI.getCableHelper().getMultipart(offset, face);
			if (validate.isValid(tile) && tile.canConnect(face).canConnect()) {
				logicTiles.add((T) tile);
			}
		}
		return logicTiles;
	}

	public static ArrayList<ILogicMonitor> getLocalMonitors(DataCablePart cable) {
		ArrayList<ILogicMonitor> logicTiles = new ArrayList();
		for (EnumFacing face : EnumFacing.values()) {
			BlockCoords offset = BlockCoords.translateCoords(cable.getCoords(), face.getOpposite());
			ILogicTile tile = LogisticsAPI.getCableHelper().getMultipart(offset, face);
			if (tile instanceof ILogicMonitor) {
				logicTiles.add((ILogicMonitor) tile);
			}
		}
		return logicTiles;
	}

	public INetworkCache addCable(IDataCable cable) {
		ArrayList<Pair<CableType, Integer>> connections = new ArrayList();
		int cableID = -1;
		int lastSize = -1;
		BlockCoords coords = cable.getCoords();
		for (EnumFacing dir : EnumFacing.values()) {
			if (cable.canConnect(dir)) {
				Pair<CableType, Integer> connection = getConnectionType(coords.getWorld(), coords.getBlockPos(), dir, cable.getCableType());
				if (connection.a != CableType.NONE && connection.b != -1) {
					List<IDataCable> cables = CableManager.getCables(connection.b);
					if (cables.size() > lastSize) {
						cableID = connection.b;
						lastSize = cables.size();
					}
					connections.add(connection);
				}
			}
		}
		CableManager.addCable(cableID == -1 ? cableID = CableManager.getNextAvailableID() : cableID, cable, true);
		for (Pair<CableType, Integer> connection : connections) {
			if (connection.b != cableID) {
				CableManager.connectNetworks(cableID, connection.b);
			}
		}
		return NetworkManager.getOrCreateNetwork(cableID);
	}

	public void removeCable(IDataCable cable) {
		CableManager.removeCable(cable.getNetworkID(), cable);
	}

	public void refreshConnections(IDataCable cable) {
		BlockCoords coords = cable.getCoords();
		for (EnumFacing dir : EnumFacing.values()) {
			Pair<CableType, Integer> connection = getConnectionType(coords.getWorld(), coords.getBlockPos(), dir, cable.getCableType());
			boolean canConnect = cable.canConnect(dir);
			if ((!canConnect && connection.a.canConnect(cable.getCableType()))) {
				cable.removeCable();
				cable.addCable();
			} else if ((canConnect && connection.a.canConnect(cable.getCableType()) && connection.b != cable.getNetworkID())) {
				CableManager.connectNetworks(cable.getNetworkID(), connection.b);
			}
		}
	}

	public INetworkCache getNetwork(TileEntity tile, EnumFacing dir) {
		Pair<CableType, Integer> connection = getConnectionType(tile.getWorld(), tile.getPos(), dir, CableType.DATA_CABLE);
		if (connection.a != CableType.NONE && connection.b != -1) {
			INetworkCache cache = NetworkManager.getNetwork(connection.b);
			if (cache != null) {
				return cache;
			}
		}
		return EmptyNetworkCache.INSTANCE;
	}

	public INetworkCache getNetwork(int registryID) {
		return NetworkManager.getNetwork(registryID);
	}

	/* public Map<BlockCoords, EnumFacing> getTileConnections(List<BlockCoords> network) { if (network == null) { return Collections.EMPTY_MAP; } Map<BlockCoords, EnumFacing> connections = new LinkedHashMap(); for (BlockCoords connect : network) { TileEntity tile = connect.getTileEntity(); if (tile != null && tile instanceof IConnectionNode) { ((IConnectionNode) tile).addConnections(connections); } else { IMultipartContainer container = MultipartHelper.getPartContainer(connect.getWorld(), connect.getBlockPos()); if (container != null) { ISlottedPart part = container.getPartInSlot(PartSlot.CENTER); if (part == null) { continue; } else if (part instanceof IDataCable && ((IDataCable) part).hasConnections()) { container.getParts().forEach(multipart -> { if (multipart instanceof IConnectionNode) { ((IConnectionNode) multipart).addConnections(connections); } }); } } } } return connections;
	 * 
	 * } public Map<BlockCoords, EnumFacing> getTileConnections(TileEntity tile, EnumFacing dir) { LinkedHashMap<BlockCoords, EnumFacing> connections = new LinkedHashMap(); int registryID = -1; CableType cableType = CableType.NONE; Object adjacent = OLDMultipartHelper.getAdjacentTile(tile, dir); if (adjacent != null) { if (adjacent instanceof IDataCable) { IDataCable cable = ((IDataCable) adjacent); if (cable.isBlocked(dir.getOpposite())) { return connections; } registryID = cable.registryID(); cableType = cable.getCableType(); } else if (adjacent instanceof IConnectionNode) { IConnectionNode node = (IConnectionNode) adjacent; ((IConnectionNode) node).addConnections(connections); } } if (registryID != -1) { try { LinkedHashMap<BlockCoords, EnumFacing> cacheList = CacheRegistry.getChannelArray(registryID); if (!cacheList.isEmpty()) { if (cableType.hasUnlimitedConnections()) { connections.putAll(cacheList); } else { for (Entry<BlockCoords, EnumFacing> entry : cacheList.entrySet()) { if (entry.getKey().getBlock(entry.getKey().getWorld()) != null) { connections.put(entry.getKey(), entry.getValue()); } } } } } catch (Exception exception) { Logistics.logger.error("CableHelper: " + exception.getLocalizedMessage()); } } return connections; } */

	public Pair<CableType, Integer> getConnectionType(World world, BlockPos pos, EnumFacing dir, CableType cableType) {
		BlockPos offset = pos.offset(dir);
		IMultipartContainer container = MultipartHelper.getPartContainer(world, offset);
		if (container != null) {
			return getConnectionType(container, dir, cableType);
		} else {
			TileEntity tile = world.getTileEntity(offset);
			if (tile != null) {
				return getConnectionTypeFromObject(tile, dir, cableType);
			}
		}
		return new Pair(CableType.NONE, -1);
	}

	/** checks what cable type can be connected via a certain direction, assumes the other block can connect from this side */
	public Pair<CableType, Integer> getConnectionType(IMultipartContainer container, EnumFacing dir, CableType cableType) {
		ISlottedPart part = container.getPartInSlot(PartSlot.getFaceSlot(dir.getOpposite()));
		if (part != null) {
			return getConnectionTypeFromObject(part, dir, cableType);
		} else {
			ISlottedPart centre = container.getPartInSlot(PartSlot.CENTER);
			if (centre != null && centre instanceof IDataCable) {
				return getConnectionTypeFromObject(centre, dir, cableType);
			}
		}
		return new Pair(CableType.NONE, -1);
	}

	public Pair<CableType, Integer> getConnectionTypeFromObject(Object connection, EnumFacing dir, CableType cableType) {
		if (connection instanceof IDataCable) {
			IDataCable cable = (IDataCable) connection;
			if (cable.getCableType().canConnect(cableType)) {
				return cable.canConnect(dir.getOpposite()) ? new Pair(cable.getCableType(), cable.getNetworkID()) : new Pair(CableType.NONE, -1);
			}
		} else if (connection instanceof ILogicTile) {
			return ((ILogicTile) connection).canConnect(dir.getOpposite()).canShowConnection() ? new Pair(CableType.BLOCK_CONNECTION, -1) : new Pair(CableType.NONE, -1);
		}
		return new Pair(CableType.NONE, -1);
	}
}
