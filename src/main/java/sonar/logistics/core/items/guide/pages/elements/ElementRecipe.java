package sonar.logistics.core.items.guide.pages.elements;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import sonar.core.SonarCore;
import sonar.logistics.core.items.guide.GuiGuide;
import sonar.logistics.core.items.guide.GuidePageRegistry;
import sonar.logistics.core.items.guide.pages.pages.IGuidePage;

import java.util.ArrayList;
import java.util.List;

public abstract class ElementRecipe<R> implements IGuidePageElement {

	public int x, y, cycle;
	public EntityPlayer player;
	public List<List<ItemStack>> stacks = Lists.newArrayList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

	public List<Integer> positions;
	public int[][] slots = setSlots();
	public static int slotSize = 24;
	public int page;
	public ItemStack stack;
	public R recipe;

	public ElementRecipe(int page, EntityPlayer player, ItemStack stack, int x, int y) {
		this.page = page;
		this.stack = stack;
		this.x = x;
		this.y = y;
		this.player = player;
		this.recipe = getRecipe();
	}

	public abstract int[][] setSlots();

	public abstract R getRecipe();

	public abstract int recipeSize();

	@Override
	public int getDisplayPage() {
		return page;
	}

	public int getSlot(int x, int y, int mouseX, int mouseY) {
		int slotX = (mouseX - x)- slotSize;
		int slotY = (mouseY - y) -8 - slotSize*2;		
		for (int pos = 0; pos < slots.length; pos++) {
			int[] slot = slots[pos];
			if (slot[0] >= slotX && slot[0] <= slotX+slotSize && slot[1] >= slotY && slot[1] <= slotY+slotSize) {
				return pos;
			}
		}
		
		return -1;
	}

	public ItemStack getStack(int pos) {
		if (pos != -1) {
			List<ItemStack> list = stacks.get(pos);
			if (!list.isEmpty()) {
				Integer cyclePos = positions.get(pos);
				return list.get(cyclePos);
			}
		}
		return null;
	}

	public List<Integer> getPos() {
		cycle++;
		if (positions == null || cycle == 40) {
			List<Integer> ints = new ArrayList<>();
			for (List<ItemStack> craft : stacks) {
				int i = craft.isEmpty() ? 0 : Math.abs(SonarCore.rand.nextInt()) % Math.max(craft.size(), 1);
				ints.add(i);
			}
			positions = ints;
			cycle = 0;
		}
		return positions;
	}

	@Override
	public void drawElement(GuiGuide gui, int x, int y, int page, int mouseX, int mouseY) {
		ItemStack stack = getStack(getSlot(x, y, mouseX, mouseY));
		if (stack != null)
			gui.drawNormalToolTip(stack, gui.getGuiLeft() + mouseX, gui.getGuiTop() + mouseY);
	}

	@Override
	public boolean mouseClicked(GuiGuide gui, IGuidePage page, int x, int y, int button) {
		ItemStack stack = getStack(getSlot(gui.getGuiLeft() + this.x, gui.getGuiTop() + this.y, x, y));
		if (stack != null) {
			IGuidePage stackPage = GuidePageRegistry.getGuidePage(stack);
			if (stackPage != null)
				gui.setCurrentPage(stackPage.pageID(), 0);
		}
		return false;
	}
}
