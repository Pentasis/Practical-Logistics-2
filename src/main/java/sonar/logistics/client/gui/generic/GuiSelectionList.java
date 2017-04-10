package sonar.logistics.client.gui.generic;

import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.client.gui.SonarButtons.ImageButton;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.RenderHelper;
import sonar.core.utils.IWorldPosition;
import sonar.logistics.client.LogisticsColours;
import sonar.logistics.client.gui.GuiLogistics;

public abstract class GuiSelectionList<T> extends GuiLogistics {

	public GuiSelectionList(Container container, IWorldPosition part) {
		super(container, part);
	}

	public boolean enableListRendering = true; // for when this is overriden and the gui has multiple states.
	public int size = 11;
	public int listHeight = 12;
	public int start, finish;
	private GuiButton rselectButton;
	public List<T> infoList = Lists.newArrayList();

	@Override
	public void initGui() {
		this.buttonList.clear();
		Keyboard.enableRepeatEvents(true);
		this.mc.thePlayer.openContainer = this.inventorySlots;
		this.xSize = xSize();
		this.ySize = ySize();
		this.size = listSize();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		if (enableListRendering) {
			scroller = new SonarScroller(this.guiLeft + 235, this.guiTop + 29, size * listHeight + 2, 10);
			for (int i = 0; i < size; i++) {
				this.buttonList.add(new SelectionButton(this, 10 + i, guiLeft + 7, guiTop + 29 + (i * listHeight), listHeight));
			}
		}
	}

	public int xSize() {
		return xSize;
	}

	public int ySize() {
		return ySize;
	}

	public int listSize() {
		return (int) Math.floor((ySize() - 29) / listHeight);
	}

	public abstract int getColour(int i, int type);

	public List<List<Integer>> getListTypes() {
		List<Integer> categories = Lists.newArrayList(), data = Lists.newArrayList(), paired = Lists.newArrayList();
		if (infoList != null) {
			for (int i = start; i < finish; i++) {
				T info = infoList.get(i);
				if (info == null)
					continue;
				if (isCategoryHeader(info))
					categories.add(i - start);
				if (isSelectedInfo(info))
					data.add(i - start);
				if (isPairedInfo(info))
					paired.add(i - start);
			}
		}
		return Lists.newArrayList(categories, data, paired);
	}

	public abstract boolean isPairedInfo(T info);

	public abstract boolean isSelectedInfo(T info);

	public abstract boolean isCategoryHeader(T info);

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		if (enableListRendering) {
			setInfo();
			start = (int) (infoSize() * scroller.getCurrentScroll());
			finish = Math.min(start + size, infoSize());
			GL11.glPushMatrix();
			GlStateManager.pushAttrib();
			GL11.glScaled(listScale(), listScale(), listScale());
			for (int i = start; i < finish; i++) {
				T info = infoList.get(i);
				if (info != null) {
					int yPos = (int) ((1.0 / listScale()) * (32 + (listHeight * i) - (listHeight * start)));
					renderInfo(info, yPos);
				}
			}
			GlStateManager.popAttrib();
			GL11.glPopMatrix();
		}
		super.drawGuiContainerForegroundLayer(x, y);

	}
	
	public double listScale(){
		return 0.75;
	}

	public abstract void renderInfo(T info, int yPos);

	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		if (enableListRendering) {
			scroller.handleMouse(needsScrollBars(), infoSize());
		}
	}

	public void drawScreen(int x, int y, float var) {
		super.drawScreen(x, y, var);
		if (enableListRendering) {
			scroller.drawScreen(x, y, needsScrollBars());
		}
	}

	@Override
	public void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);

		if (enableListRendering) {
			if (button == 1) {
				for (int l = 0; l < this.buttonList.size(); ++l) {
					GuiButton guibutton = (GuiButton) this.buttonList.get(l);
					if (guibutton.mousePressed(this.mc, x, y)) {
						ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(this, guibutton, this.buttonList);
						if (MinecraftForge.EVENT_BUS.post(event))
							break;
						this.rselectButton = event.getButton();
						event.getButton().playPressSound(this.mc.getSoundHandler());
						this.buttonPressed(event.getButton(), 1);
						if (this.equals(this.mc.currentScreen))
							MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(this, event.getButton(), this.buttonList));
					}
				}
			}
		}
	}

	public void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);

		if (enableListRendering) {
			if (this.rselectButton != null && state == 1) {
				this.rselectButton.mouseReleased(mouseX, mouseY);
				this.rselectButton = null;
			}
		}
	}

	public void buttonPressed(GuiButton button, int buttonID) {
		if (enableListRendering) {
			if (button != null && button.id >= 10) {
				int start = (int) (infoSize() * scroller.getCurrentScroll());
				int network = start + button.id - 10;
				if (network < infoSize()) {
					T info = infoList.get(network);
					if (info != null) {
						selectionPressed(button, network, buttonID, info);
					}
				}
			}
		}
	}

	public abstract void selectionPressed(GuiButton button, int infoPos, int buttonID, T info);

	public void actionPerformed(GuiButton button) {
		if (enableListRendering) {
			this.buttonPressed(button, 0);
		}
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		if (enableListRendering) {
			int width = 225;
			int left = guiLeft + 7;

			List<List<Integer>> data = getListTypes();
			for (int i = 0; i < size; i++) {
				int top = guiTop + 29 + (listHeight * i);
				if (!data.get(2).contains(i)) {
					drawInfoBackground(data, width, listHeight, left, top, i);
				} else {
					drawSelectedInfoBackground(width, listHeight, left, top, i);
				}
			}
		}
		RenderHelper.restoreBlendState();
	}

	public void drawInfoBackground(List<List<Integer>> data, int width, int height, int left, int top, int i) {
		int mainColour = data.get(0).contains(i) ? LogisticsColours.category.getRGB() : (data.get(1).contains(i)) ? getColour(i, 0) : LogisticsColours.layers[1].getRGB();
		drawRect(left + 1, top + 1, left - 1 + width, top - 1 + height, mainColour);
		drawRect(left, top, left + width, top + height, LogisticsColours.layers[2].getRGB());
	}

	public void drawSelectedInfoBackground(int width, int height, int left, int top, int i) {
		int rgb = getColour(i, 1);
		drawRect(left, top, left + width, top + height, LogisticsColours.layers[2].getRGB());
		drawRect(left + 1, top + 1, left - 1 + width, top - 1 + height, LogisticsColours.grey_base.getRGB());
		drawHorizontalLine(left + 1, left - 2 + width, top + 1, rgb);
		drawHorizontalLine(left + 1, left - 2 + width, top + listHeight-1, rgb);
		drawVerticalLine(left + 1, top + 1, top + listHeight-1, rgb);
		drawVerticalLine(left - 2 + width, top + 1, top + listHeight-1, rgb);
	}

	public boolean needsScrollBars() {
		if (infoSize() <= size)
			return false;
		return true;
	}	

	public void onSelectionHovered(T info, int x,int y){
		
	}
	
	public void onButtonHovered(SelectionButton button, int x, int y){
		if (button != null && button.id >= 10) {
			int start = (int) (infoSize() * scroller.getCurrentScroll());
			int network = start + button.id - 10;
			if (network < infoSize()) {
				T info = infoList.get(network);
				if (info != null) {
					onSelectionHovered(info, x, y);
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static class SelectionButton extends ImageButton {

		GuiSelectionList list;
		public SelectionButton(GuiSelectionList list, int id, int x, int y, int listHeight) {
			super(id, x, y, null, 0, 224, 154 + 72, listHeight - 1);
			this.list=list;
		}

		public void drawButton(Minecraft mc, int x, int y) {
			this.hovered = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
			if(this.hovered){
				list.onButtonHovered(this, x, y);
			}
		}
	}

	public int infoSize() {
		return infoList == null ? 0 : infoList.size();
	}

	public abstract void setInfo();

	@Override
	public ResourceLocation getBackground() {
		return null;
	}

}
