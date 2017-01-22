package sonar.logistics.common.multiparts;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.SyncEnum;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.Pair;
import sonar.core.utils.SortingDirection;
import sonar.logistics.Logistics;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.LogicInfoList;
import sonar.logistics.api.info.monitor.ChannelType;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.settings.FluidReader;
import sonar.logistics.client.gui.GuiFluidReader;
import sonar.logistics.common.containers.ContainerFluidReader;
import sonar.logistics.connections.monitoring.FluidMonitorHandler;
import sonar.logistics.connections.monitoring.MonitoredFluidStack;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.helpers.FluidHelper;
import sonar.logistics.network.SyncMonitoredType;

public class FluidReaderPart extends ReaderMultipart<MonitoredFluidStack> implements IByteBufTile {

	public SyncMonitoredType<MonitoredFluidStack> selected = new SyncMonitoredType<MonitoredFluidStack>(1);
	public SyncEnum<FluidReader.Modes> setting = (SyncEnum) new SyncEnum(FluidReader.Modes.values(), 2).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT targetSlot = (INT) new SyncTagType.INT(3).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT posSlot = (INT) new SyncTagType.INT(4).addSyncType(SyncType.SPECIAL);
	public SyncEnum<SortingDirection> sortingOrder = (SyncEnum) new SyncEnum(SortingDirection.values(), 5).addSyncType(SyncType.SPECIAL);
	public SyncEnum<FluidReader.SortingType> sortingType = (SyncEnum) new SyncEnum(FluidReader.SortingType.values(), 6).addSyncType(SyncType.SPECIAL);
	{
		syncList.addParts(setting, targetSlot, posSlot, sortingOrder, sortingType, selected);
	}

	public FluidReaderPart() {
		super(FluidMonitorHandler.id);
	}

	public FluidReaderPart(EnumFacing face) {
		super(FluidMonitorHandler.id, face);
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.fluidReaderPart);
	}

	@Override
	public MonitoredList<MonitoredFluidStack> sortMonitoredList(MonitoredList<MonitoredFluidStack> updateInfo, int channelID) {
		FluidHelper.sortFluidList(updateInfo, sortingOrder.getObject(), sortingType.getObject());
		return updateInfo;
	}

	@Override
	public ChannelType channelType() {
		return ChannelType.UNLIMITED;
	}

	@Override
	public void setMonitoredInfo(MonitoredList<MonitoredFluidStack> updateInfo, int channelID) {
		IMonitorInfo info = null;
		switch (setting.getObject()) {
		case SELECTED:
			MonitoredFluidStack stack = selected.getMonitoredInfo();
			if (stack != null && stack.isValid()) {
				stack.fluidStack.getObject().stored=0;
				MonitoredFluidStack dummyInfo = stack.copy();
				Pair<Boolean, IMonitorInfo> latestInfo = updateInfo.getLatestInfo(dummyInfo);
				info = latestInfo.a ? latestInfo.b : dummyInfo;
			}
			break;
		case POS:
			break;
		case STORAGE:
			break;
		case TANKS:
			info = new LogicInfoList(getIdentity(), MonitoredFluidStack.id, this.getNetworkID());
			break;
		default:
			break;
		}
		if (info != null) {
			InfoUUID id = new InfoUUID(getIdentity().hashCode(), 0);
			IMonitorInfo oldInfo = Logistics.getServerManager().info.get(id);
			if (oldInfo == null || !oldInfo.isMatchingType(info) || !oldInfo.isIdenticalInfo(info)) {
				Logistics.getServerManager().changeInfo(id, info);
			}
		}

	}

	@Override
	public Object getServerElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new ContainerFluidReader(this, player);
		}
		return null;
	}

	@Override
	public Object getClientElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new GuiFluidReader(this, player);
		}
		return null;
	}

	@Override
	public String getDisplayName() {
		return FontHelper.translate("item.FluidReader.name");
	}

}
