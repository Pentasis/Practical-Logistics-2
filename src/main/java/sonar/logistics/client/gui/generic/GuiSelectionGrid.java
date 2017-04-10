package sonar.logistics.client.gui.generic;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.RenderHelper;
import sonar.core.utils.IWorldPosition;
import sonar.logistics.PL2Constants;
import sonar.logistics.api.info.IMonitorInfo;
import sonar.logistics.api.utils.MonitoredList;
import sonar.logistics.client.LogisticsColours;
import sonar.logistics.client.gui.GuiLogistics;

public abstract class GuiSelectionGrid<T extends IMonitorInfo> extends GuiLogistics {

	public static final ResourceLocation sorting_icons = new ResourceLocation(PL2Constants.MODID + ":textures/gui/sorting_icons.png");

	public abstract void onGridClicked(T selection, int pos, int button, boolean empty);

	public abstract void renderSelection(T selection, int x, int y, int slot);

	public abstract void renderStrings(int x, int y);

	public abstract void renderToolTip(T selection, int x, int y);

	public abstract MonitoredList<T> getGridList();

	public int getGridSize(MonitoredList<T> list) {
		return getGridList() == null ? 0 : list.size();
	}

	private boolean needsScrollBars(MonitoredList<T> list) {
		if (getGridSize(list) <= (12 * 7))
			return false;
		return true;
	}

	public GuiSelectionGrid(Container container, IWorldPosition entity) {
		super(container, entity);
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.mc.thePlayer.openContainer = this.inventorySlots;
		this.xSize = 176 + 72;
		this.ySize = 256;

		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		scroller = new SonarScroller(this.guiLeft + 164 + 68, this.guiTop + 31, 128, 10);
	}

	@Override
	public void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		if (button == 0 || button == 1) {
			MonitoredList<T> list = getGridList().copyInfo();
			if (x - guiLeft >= 13 && x - guiLeft <= 13 + (12 * 18) && y - guiTop >= 32 && y - guiTop <= 32 + (7 * 18)) {
				int start = (int) (getGridSize(list) / 12 * scroller.getCurrentScroll());
				int X = (x - guiLeft - 13) / 18;
				int Y = (y - guiTop - 32) / 18;
				int i = (start * 12) + (12 * Y) + X;
				if (i < getGridList().size()) {
					T storedStack = getGridList().get(i);
					if (storedStack != null) {
						onGridClicked(storedStack, i, button, false);
						return;
					}
				}
				onGridClicked(null, i, button, true);
			}
		}
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
		renderStrings(x, y);		   

		preRender();
		MonitoredList<T> list = getGridList().copyInfo();
		if (list != null && !list.isEmpty()) {
			int start = (int) (getGridSize(list) / 12 * scroller.getCurrentScroll());
			int i = start * 12;
			int finish = Math.min(i + (12 * 7), getGridSize(list));
			for (int Y = 0; Y < 7; Y++) {
				for (int X = 0; X < 12; X++) {
					if (i < finish) {
						T selection = list.get(i);
						if (selection != null) {
							renderSelection(selection, X, Y, i);
						}
					}
					i++;
				}
			}
		}
		postRender();		
		
		if (x - guiLeft >= 13 && x - guiLeft <= 13 + (12 * 18) && y - guiTop >= 32 && y - guiTop <= 32 + (7 * 18)) {
			int start = (int) (getGridSize(list) / 12 * scroller.getCurrentScroll());
			int X = (x - guiLeft - 13) / 18;
			int Y = (y - guiTop - 32) / 18;
			int i = (start * 12) + X + ((Y) * 12);

			if (list != null) {
				if (i < list.size()) {
					T selection = list.get(i);
					if (selection != null) {

						GL11.glDisable(GL11.GL_DEPTH_TEST);
						GL11.glDisable(GL11.GL_LIGHTING);
						this.renderToolTip(selection, x - guiLeft, y - guiTop);
						GL11.glEnable(GL11.GL_LIGHTING);
						GL11.glEnable(GL11.GL_DEPTH_TEST);
						net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();

					}
				}
			}
		}
		//GlStateManager.disableCull();
	}

	public void preRender() {}

	public void postRender() {}

	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		MonitoredList<T> list = getGridList().copyInfo();
		scroller.handleMouse(needsScrollBars(list), getGridSize(list));
	}

	public void drawScreen(int x, int y, float var) {
		super.drawScreen(x, y, var);
		MonitoredList<T> list = getGridList().copyInfo();
		scroller.drawScreen(x, y, needsScrollBars(list));
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		this.renderPlayerInventory(40, 173);

		drawRect(guiLeft+12, guiTop+ 31, guiLeft+228, guiTop+ 157, LogisticsColours.grey_base.getRGB());
		drawRect(guiLeft+13, guiTop+ 32, guiLeft+227, guiTop+ 156, LogisticsColours.blue_overlay.getRGB());
		
		drawRect(guiLeft+12, guiTop+ 170, guiLeft+xSize-9, guiTop+ 252, LogisticsColours.grey_base.getRGB());
		drawRect(guiLeft+13, guiTop+ 171, guiLeft+xSize-10, guiTop+ 251, LogisticsColours.blue_overlay.getRGB());
		RenderHelper.restoreBlendState();
	}

}
