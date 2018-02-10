package sonar.logistics.api.displays.elements;

import org.lwjgl.opengl.GL11;

import static net.minecraft.client.renderer.GlStateManager.*;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.helpers.RenderHelper;
import sonar.logistics.api.displays.IDisplayElementList;

public class ItemStackElement extends AbstractDisplayElement {

	public StoredItemStack stack;

	public ItemStackElement(IDisplayElementList list, StoredItemStack stack) {
		super(list);
		this.stack = stack;
	}

	public void render() {
		scale(1, 1 , 0.1); //compresses the item on the z axis
		//enablePolygonOffset();
		GL11.glRotated(180, 0, 1, 0); // flips the item
		GL11.glScaled(-1, 1, 1);
		//doPolygonOffset(-1, -1);
		RenderHelper.renderItemIntoGUI(getItem(), 0, 0);
		//disablePolygonOffset();
		GlStateManager.translate(0, 0, 2);
		GlStateManager.depthMask(false);
		RenderHelper.renderStoredItemStackOverlay(getItem(), 0, 0, 0, "" + stack.stored, false);
		GlStateManager.depthMask(true);
		
		/*
		ItemStack item = stack.item;
		GL11.glPushMatrix();
		GlStateManager.enableDepth();
		GL11.glTranslated(-(1 - width / 2 - 0.0625), -0.68 + height / 2, 0.00);
		GL11.glRotated(180, 0, 1, 0);
		GL11.glScaled(-1, 1, 1);
		GlStateManager.disableLighting();
		GlStateManager.enablePolygonOffset();
		GlStateManager.doPolygonOffset(-1, -1);
		GlStateManager.enableCull();
		RenderHelper.renderItemIntoGUI(item, 0, 0);
		GlStateManager.disablePolygonOffset();
		GlStateManager.translate(0, 0, 2);
		GlStateManager.depthMask(false);
		GlStateManager.depthMask(true);
		GlStateManager.disableDepth();
		GL11.glPopMatrix();
		*/
	}

	public StoredItemStack getStoredItem() {
		return stack;
	}

	public StoredItemStack setStoredItem(StoredItemStack item) {
		return stack = item;
	}

	public ItemStack getItem() {
		return stack.getItemStack();
	}

	public ItemStack setItem(ItemStack item) {
		return stack.item = item;
	}

	public long getCount() {
		return stack.stored;
	}

	public long setCount(long stored) {
		return stack.stored = stored;
	}

	@Override
	public String getRepresentiveString() {
		return getItem().getDisplayName() + " - " + getCount();
	}

	@Override
	int[] createUnscaledWidthHeight() {
		return new int[] { 16, 16 };
	}

}