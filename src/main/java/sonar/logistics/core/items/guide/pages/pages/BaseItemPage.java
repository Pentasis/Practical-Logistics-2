package sonar.logistics.core.items.guide.pages.pages;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.logistics.core.items.guide.GuiGuide;
import sonar.logistics.core.items.guide.pages.elements.*;

import java.util.ArrayList;
import java.util.List;

public class BaseItemPage extends GeneralPage implements IGuidePage {

	public String unlocalizedName;
	public ItemStack stack;
	public List<ElementLink> links = new ArrayList<>();
	public double rotate = 0;

	public BaseItemPage(int pageID, ItemStack stack) {
		this(pageID, stack, "guide." + stack.getUnlocalizedName().substring(5) + ".name");
	}

	public BaseItemPage(int pageID, ItemStack stack, String... descKey) {
		super(pageID, "", descKey);
		this.stack = stack;
		this.unlocalizedName = stack.getUnlocalizedName();
	}

	public String getDisplayName() {
		return stack.getDisplayName();
	}

	public ItemStack getItemStack() {
		return stack;
	}

	//// CREATE \\\\
	
	public List<IGuidePageElement> getElements(GuiGuide gui, List<IGuidePageElement> elements) {
		super.getElements(gui, elements);
		elements.add(new ElementItem(0, stack, 4, 15));
		ElementCraftingRecipe recipe = new ElementCraftingRecipe(0, gui.mc.player, stack, 4, 104);
		if (recipe.recipe != null) {
			elements.add(recipe);
		} else {
			ElementHammerRecipe hammerR = new ElementHammerRecipe(0, gui.mc.player, stack, 4, 74);
			if (hammerR.recipe != null) {
				elements.add(hammerR);
			}
		}

		return elements;
	}

	//// DRAWING \\\\

	public void drawPageInGui(GuiGuide gui, int yPos) {
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.saveBlendState();
		GlStateManager.enableDepth();
		RenderHelper.renderItem(gui, 8, yPos - 1, stack);
		RenderHelper.renderStoredItemStackOverlay(stack, stack.getCount() == 1 ? 0 : stack.getCount(), 8, yPos - 1, null, true);
		GlStateManager.disableDepth();
		RenderHelper.restoreBlendState();
		FontHelper.text(stack.getDisplayName(), 28, yPos + 3, -1);
	}
}