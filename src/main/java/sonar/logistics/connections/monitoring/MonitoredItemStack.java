package sonar.logistics.connections.monitoring;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import mcmultipart.raytrace.PartMOP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.BlockInteractionType;
import sonar.core.helpers.RenderHelper;
import sonar.core.network.sync.SyncNBTAbstract;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.displays.DisplayType;
import sonar.logistics.api.displays.IDisplayInfo;
import sonar.logistics.api.displays.InfoContainer;
import sonar.logistics.api.info.IBasicClickableInfo;
import sonar.logistics.api.info.IComparableInfo;
import sonar.logistics.api.info.IJoinableInfo;
import sonar.logistics.api.info.IMonitorInfo;
import sonar.logistics.api.info.INameableInfo;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.info.types.BaseInfo;

@LogicInfoType(id = MonitoredItemStack.id, modid = Logistics.MODID)
public class MonitoredItemStack extends BaseInfo<MonitoredItemStack> implements IJoinableInfo<MonitoredItemStack>, IBasicClickableInfo, INameableInfo<MonitoredItemStack>, IComparableInfo<MonitoredItemStack> {

	public static final String id = "item";
	public static LogicMonitorHandler<MonitoredItemStack> handler = LogicMonitorHandler.instance(ItemMonitorHandler.id);
	public final SyncNBTAbstract<StoredItemStack> itemStack = new SyncNBTAbstract<StoredItemStack>(StoredItemStack.class, 0);
	public final SyncTagType.INT networkID = (INT) new SyncTagType.INT(1).setDefault(-1);
	{
		syncParts.addParts(itemStack, networkID);
	}

	public MonitoredItemStack() {
	}

	public MonitoredItemStack(StoredItemStack stack, int networkID) {
		this(stack);
		this.networkID.setObject(networkID);
	}

	public MonitoredItemStack(StoredItemStack stack) {
		this.itemStack.setObject(stack);
	}

	@Override
	public boolean isIdenticalInfo(MonitoredItemStack info) {
		return itemStack.getObject().equals(info.itemStack.getObject()) && networkID.getObject().equals(networkID.getObject());
	}

	@Override
	public boolean isMatchingInfo(MonitoredItemStack info) {
		return itemStack.getObject().equalStack(info.itemStack.getObject().item);
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return info instanceof MonitoredItemStack;
	}

	@Override
	public LogicMonitorHandler<MonitoredItemStack> getHandler() {
		return handler;
	}

	@Override
	public boolean canJoinInfo(MonitoredItemStack info) {
		return isMatchingInfo(info);
	}

	@Override
	public IJoinableInfo joinInfo(MonitoredItemStack info) {
		itemStack.getObject().add(info.itemStack.getObject());
		return this;
	}

	@Override
	public boolean isValid() {
		return itemStack.getObject() != null && itemStack.getObject().item != null;
	}

	@Override
	public String getID() {
		return id;
	}

	public String toString() {
		if (itemStack.getObject() != null)
			return itemStack.getObject().toString();
		return super.toString() + " : NULL";
	}

	@Override
	public MonitoredItemStack copy() {
		return new MonitoredItemStack(itemStack.getObject().copy(), networkID.getObject());
	}

	@Override
	public void renderInfo(InfoContainer container, IDisplayInfo displayInfo, double width, double height, double scale, int infoPos) {
		if (itemStack.getObject() != null) {
			DisplayType type = container.display.getDisplayType();
			GlStateManager.enableDepth();
			StoredItemStack stack = itemStack.getObject();
			ItemStack item = stack.item;
			GL11.glPushMatrix();
			GL11.glTranslated(-(1 - width / 2 - 0.0625), -0.68 + height / 2, 0.00);
			GL11.glRotated(180, 0, 1, 0);
			GL11.glScaled(-1, 1, 1);
			double actualScale = type == DisplayType.LARGE ? scale * 3 : scale * 2;

			GL11.glScaled(actualScale, actualScale, 0.01);
			double trans = type == DisplayType.SMALL ? 4 : -(7);
			GL11.glTranslated(-8, -8, 0);
			GlStateManager.disableLighting();
			GlStateManager.enablePolygonOffset();
			GlStateManager.doPolygonOffset(-1, -1);
			GlStateManager.enableCull();
			RenderHelper.renderItemIntoGUI(item, 0, 0);
			GlStateManager.disablePolygonOffset();
			GlStateManager.translate(0, 0, 2);
			GlStateManager.depthMask(false);
			RenderHelper.renderStoredItemStackOverlay(item, 0, 0, 0, "" + stack.stored, false);
			GlStateManager.depthMask(true);
			GL11.glPopMatrix();
			GlStateManager.enableDepth();
		}
	}

	@Override
	public boolean onStandardClick(BlockInteractionType type, boolean doubleClick, IDisplayInfo renderInfo, EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit, InfoContainer container) {
		if (InfoHelper.canBeClickedStandard(renderInfo.getRenderProperties(), player, hand, stack, hit)) {
			if (!player.getEntityWorld().isRemote)
				InfoHelper.screenItemStackClicked(itemStack.getObject(), networkID.getObject(), type, doubleClick, renderInfo.getRenderProperties(), player, hand, stack, hit);
			return true;
		}
		return false;
	}

	@Override
	public String getClientIdentifier() {
		return "Item: " + (itemStack.getObject() != null && itemStack.getObject().getItemStack() != null ? itemStack.getObject().getItemStack().getDisplayName() : "ITEMSTACK");
	}

	@Override
	public String getClientObject() {
		return itemStack.getObject() != null ? "" + itemStack.getObject().stored : "ERROR";
	}

	@Override
	public String getClientType() {
		return "item";
	}

	@Override
	public void getComparableObjects(Map<String, Object> objects) {
		StoredItemStack stack = itemStack.getObject();
		objects.put("items", stack);
		
		objects.put("nbt", stack!=null? stack.getTagCompound() : new NBTTagCompound());
		
		objects.put("damage", stack != null ? stack.getItemDamage() : -1);

	}

}
