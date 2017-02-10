package sonar.logistics.common.multiparts;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.inventory.SonarMultipartInventory;
import sonar.core.network.sync.SyncEnum;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.Pair;
import sonar.core.utils.SortingDirection;
import sonar.logistics.Logistics;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.cabling.ChannelType;
import sonar.logistics.api.info.IMonitorInfo;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.nodes.NodeConnection;
import sonar.logistics.api.readers.InventoryReader;
import sonar.logistics.api.viewers.ViewerType;
import sonar.logistics.client.gui.GuiChannelSelection;
import sonar.logistics.client.gui.GuiInventoryReader;
import sonar.logistics.common.containers.ContainerChannelSelection;
import sonar.logistics.common.containers.ContainerInventoryReader;
import sonar.logistics.connections.monitoring.ItemMonitorHandler;
import sonar.logistics.connections.monitoring.MonitoredEnergyStack;
import sonar.logistics.connections.monitoring.MonitoredItemStack;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.helpers.ItemHelper;
import sonar.logistics.info.LogicInfoRegistry.RegistryType;
import sonar.logistics.info.types.LogicInfo;
import sonar.logistics.info.types.LogicInfoList;
import sonar.logistics.info.types.ProgressInfo;

public class InventoryReaderPart extends ReaderMultipart<MonitoredItemStack> implements IByteBufTile {

	public SonarMultipartInventory inventory = new SonarMultipartInventory(this, 1);
	public SyncEnum<InventoryReader.Modes> setting = (SyncEnum) new SyncEnum(InventoryReader.Modes.values(), 2).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT targetSlot = (INT) new SyncTagType.INT(3).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT posSlot = (INT) new SyncTagType.INT(4).addSyncType(SyncType.SPECIAL);
	public SyncEnum<SortingDirection> sortingOrder = (SyncEnum) new SyncEnum(SortingDirection.values(), 5).addSyncType(SyncType.SPECIAL);
	public SyncEnum<InventoryReader.SortingType> sortingType = (SyncEnum) new SyncEnum(InventoryReader.SortingType.values(), 6).addSyncType(SyncType.SPECIAL);
	{
		syncList.addParts(inventory, setting, targetSlot, posSlot, sortingOrder, sortingType);
	}

	public InventoryReaderPart() {
		super(ItemMonitorHandler.id);
	}

	public InventoryReaderPart(EnumFacing face) {
		super(ItemMonitorHandler.id, face);
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.inventoryReaderPart);
	}

	@Override
	public MonitoredList<MonitoredItemStack> sortMonitoredList(MonitoredList<MonitoredItemStack> updateInfo, int channelID) {
		ItemHelper.sortItemList(updateInfo, sortingOrder.getObject(), sortingType.getObject());
		return updateInfo;
	}

	@Override
	public ChannelType channelType() {
		return ChannelType.UNLIMITED;
	}

	@Override
	public void setMonitoredInfo(MonitoredList<MonitoredItemStack> updateInfo, ArrayList<NodeConnection> connections, ArrayList<Entity> entities, int channelID) {
		IMonitorInfo info = null;
		switch (setting.getObject()) {
		case INVENTORIES:
			info = new LogicInfoList(getIdentity(), MonitoredItemStack.id, this.getNetworkID());
			break;
		case POS:
			int pos = posSlot.getObject();
			if (pos < updateInfo.size())
				info = updateInfo.get(posSlot.getObject());
			break;
		case SLOT:
			// make a way of getting the slot
			break;
		case STACK:
			ItemStack stack = inventory.getStackInSlot(0);
			if (stack != null) {
				MonitoredItemStack dummyInfo = new MonitoredItemStack(new StoredItemStack(stack.copy(), 0), network.getNetworkID());
				Pair<Boolean, IMonitorInfo> latestInfo = updateInfo.getLatestInfo(dummyInfo);
				if (latestInfo.b instanceof MonitoredItemStack) {
					((MonitoredItemStack) latestInfo.b).networkID.setObject(network.getNetworkID());
				}
				info = latestInfo.a ? latestInfo.b : dummyInfo;
			}
			break;
		case STORAGE:
			info = new ProgressInfo(LogicInfo.buildDirectInfo("item.storage", RegistryType.TILE, updateInfo.sizing.getStored(), null), LogicInfo.buildDirectInfo("max", RegistryType.TILE, updateInfo.sizing.getMaxStored(), null));
			break;
		default:
			break;
		}
		if (info != null) {
			InfoUUID id = new InfoUUID(getIdentity().hashCode(), 0);
			IMonitorInfo oldInfo = Logistics.getServerManager().info.get(id);
			if (oldInfo == null || !oldInfo.isMatchingType(info) || !oldInfo.isMatchingInfo(info) || !oldInfo.isIdenticalInfo(info)) {
				Logistics.getServerManager().changeInfo(id, info);
			}
		}
	}

	public void readPacket(ByteBuf buf, int id) {
		super.readPacket(buf, id);

		// when the order of the list is changed the viewers need to recieve a full update
		if (id == 5 || id == 6) {
			ArrayList<EntityPlayer> players = viewers.getViewers(true, ViewerType.INFO);
			for (EntityPlayer player : players) {
				viewers.addViewer(player, ViewerType.TEMPORARY);
			}
		}
	}

	@Override
	public Object getServerElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch(id){
		case 0:
			return new ContainerInventoryReader(this, player);
		case 1:
			return new ContainerChannelSelection(this);
		}		
		return null;
	}

	@Override
	public Object getClientElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch(id){
		case 0:
			return new GuiInventoryReader(this, player);
		case 1:
			return new GuiChannelSelection(player, this, 0);
		}		
		return null;
	}

	@Override
	public String getDisplayName() {
		return FontHelper.translate("item.InventoryReader.name");
	}

}
