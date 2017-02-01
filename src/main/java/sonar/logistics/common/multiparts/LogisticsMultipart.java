package sonar.logistics.common.multiparts;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import sonar.core.integration.multipart.SonarMultipart;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.logistics.api.cabling.ILogicTile;
import sonar.logistics.api.connecting.EmptyNetworkCache;
import sonar.logistics.api.connecting.INetworkCache;
import sonar.logistics.api.operator.IOperatorProvider;

public abstract class LogisticsMultipart extends SonarMultipart implements ILogicTile, IOperatorProvider {

	public INetworkCache network = EmptyNetworkCache.INSTANCE;
	public SyncTagType.INT networkID = (INT) new SyncTagType.INT(0).setDefault(-1);
	public static final PropertyDirection ORIENTATION = PropertyDirection.create("facing");
	public static final PropertyDirection ROTATION = PropertyDirection.create("rotation");
	{
		syncList.addPart(networkID);
	}

	public LogisticsMultipart() {
		super();
	}

	public LogisticsMultipart(AxisAlignedBB collisionBox) {
		super(collisionBox);
	}

	public void setLocalNetworkCache(INetworkCache network) {
		if (!this.wasRemoved) {
			this.network = network;
			this.networkID.setObject(network.getNetworkID());
		}
	}

	public INetworkCache getNetwork() {
		return network;
	}

	public void addInfo(List<String> info) {
		ItemStack dropStack = getItemStack();
		if (dropStack != null)
			info.add(TextFormatting.UNDERLINE + dropStack.getDisplayName());
		info.add("Network ID: " + networkID.getObject());
		info.add("Has channels: " + (this instanceof InfoReaderPart));
	}

	public int getNetworkID() {
		return networkID.getObject();
	}

	public void updateOperatorInfo() {
		this.requestSyncPacket();
	}

	@Override
	public List<ItemStack> getDrops() {
		return Lists.newArrayList(this.getItemStack());
	}
}
