package sonar.logistics.connections.monitoring;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.FontHelper;
import sonar.core.network.sync.SyncNBTAbstract;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.displays.IDisplayInfo;
import sonar.logistics.api.displays.InfoContainer;
import sonar.logistics.api.info.IComparableInfo;
import sonar.logistics.api.info.IJoinableInfo;
import sonar.logistics.api.info.IMonitorInfo;
import sonar.logistics.api.info.INameableInfo;
import sonar.logistics.api.logistics.ComparableObject;
import sonar.logistics.helpers.InfoRenderer;
import sonar.logistics.info.types.BaseInfo;
import sonar.logistics.network.SyncMonitoredType;

@LogicInfoType(id = MonitoredEnergyStack.id, modid = Logistics.MODID)
public class MonitoredEnergyStack extends BaseInfo<MonitoredEnergyStack> implements IJoinableInfo<MonitoredEnergyStack>, INameableInfo<MonitoredEnergyStack>, IComparableInfo<MonitoredEnergyStack> {

	public static final String id = "energy";
	public static LogicMonitorHandler<MonitoredEnergyStack> handler = LogicMonitorHandler.instance(EnergyMonitorHandler.id);
	public SyncNBTAbstract<StoredEnergyStack> energyStack = new SyncNBTAbstract<StoredEnergyStack>(StoredEnergyStack.class, 0);
	public SyncMonitoredType<MonitoredBlockCoords> coords = new SyncMonitoredType<MonitoredBlockCoords>(1);
	public final SyncNBTAbstract<StoredItemStack> dropStack = new SyncNBTAbstract<StoredItemStack>(StoredItemStack.class, 3);

	{
		syncList.addParts(energyStack, coords, dropStack);
	}

	public MonitoredEnergyStack() {
	}

	public MonitoredEnergyStack(StoredEnergyStack stack, MonitoredBlockCoords coords, StoredItemStack dropstack) {
		this.energyStack.setObject(stack);
		this.coords.setInfo(coords);
		this.dropStack.setObject(dropstack);

	}

	public MonitoredEnergyStack(StoredEnergyStack stack, MonitoredBlockCoords coords) {
		this.energyStack.setObject(stack);
		this.coords.setInfo(coords);

		Item item = Item.getItemFromBlock(coords.syncCoords.getCoords().getBlockState().getBlock());
		if (item != null) {
			this.dropStack.setObject(new StoredItemStack(new ItemStack(item, 1)));
		}

	}

	@Override
	public boolean isIdenticalInfo(MonitoredEnergyStack info) {
		return energyStack.getObject().equals(info.energyStack.getObject()) && coords.getMonitoredInfo().isIdenticalInfo(info.coords.getMonitoredInfo());
	}

	@Override
	public boolean isMatchingInfo(MonitoredEnergyStack info) {
		return (energyStack.getObject().energyType == null || energyStack.getObject().energyType.equals(info.energyStack.getObject().energyType)) && coords.getMonitoredInfo().isMatchingInfo(info.coords.getMonitoredInfo()) && dropStack.getObject() == null ? info.dropStack.getObject() == null : dropStack.getObject().equals(info.dropStack.getObject());
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return info instanceof MonitoredEnergyStack;
	}

	@Override
	public LogicMonitorHandler<MonitoredEnergyStack> getHandler() {
		return handler;
	}

	@Override
	public boolean canJoinInfo(MonitoredEnergyStack info) {
		return false;// isMatchingInfo(info);
	}

	@Override
	public IJoinableInfo joinInfo(MonitoredEnergyStack info) {
		energyStack.getObject().add(info.energyStack.getObject());
		return this;
	}

	@Override
	public boolean isValid() {
		return energyStack.getObject() != null && energyStack.getObject().energyType != null && coords.getMonitoredInfo() != null;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public MonitoredEnergyStack copy() {
		return new MonitoredEnergyStack(energyStack.getObject().copy(), coords.getMonitoredInfo().copy(), dropStack.getObject().copy());
	}

	@Override
	public void renderInfo(InfoContainer container, IDisplayInfo displayInfo, double width, double height, double scale, int infoPos) {
		GL11.glPushMatrix();
		GL11.glPushMatrix();
		GlStateManager.disableLighting();
		GL11.glTranslated(-1, -+0.0625 * 12, +0.004);
		Minecraft.getMinecraft().getTextureManager().bindTexture(InfoContainer.getColour(infoPos));
		InfoRenderer.renderProgressBar(width, height, scale, energyStack.obj.stored, energyStack.obj.capacity);
		GlStateManager.enableLighting();
		GL11.glTranslated(0, 0, -0.001);
		GL11.glPopMatrix();
		InfoRenderer.renderNormalInfo(container.display.getDisplayType(), width, height, scale, displayInfo.getFormattedStrings());		
		GL11.glPopMatrix();
	}

	@Override
	public String getClientIdentifier() {
		return (energyStack.getObject() != null && energyStack.getObject().energyType != null ? energyStack.getObject().energyType.getName() : "ENERGYSTACK");
	}

	@Override
	public String getClientObject() {
		return energyStack.getObject() != null ? "" + FontHelper.formatStorage(energyStack.obj.energyType, energyStack.getObject().stored) + " / " +FontHelper.formatStorage(energyStack.obj.energyType, energyStack.getObject().capacity) : "ERROR";
	}

	@Override
	public String getClientType() {
		return "energy";
	}

	@Override
	public ArrayList<ComparableObject> getComparableObjects(ArrayList<ComparableObject> objects) {
		BlockCoords blockCoords = coords.getMonitoredInfo().syncCoords.getCoords();
		StoredEnergyStack stack = energyStack.getObject();
		objects.add(new ComparableObject(this, "x", blockCoords.getX()));
		objects.add(new ComparableObject(this, "y", blockCoords.getY()));
		objects.add(new ComparableObject(this, "z", blockCoords.getZ()));
		objects.add(new ComparableObject(this, "input", stack.input));
		objects.add(new ComparableObject(this, "output", stack.output));
		objects.add(new ComparableObject(this, "stored", stack.stored));
		objects.add(new ComparableObject(this, "capacity", stack.capacity));
		objects.add(new ComparableObject(this, "types", stack.energyType.toString()));
		return objects;
	}

}
