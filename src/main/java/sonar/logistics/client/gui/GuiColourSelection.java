package sonar.logistics.client.gui;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.inventory.Container;
import sonar.core.client.gui.SonarTextField;
import sonar.core.helpers.FontHelper;
import sonar.core.utils.CustomColour;
import sonar.core.utils.IWorldPosition;

public class GuiColourSelection extends GuiLogistics {

	public SonarTextField r, g, b;
	public int configured = -1;

	public GuiColourSelection(Container container, IWorldPosition entity) {
		super(container, entity);
		configured = getCurrentColour();
		this.ySize = 64;
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		CustomColour colour = new CustomColour(configured);
		r = new SonarTextField(2, fontRenderer, 18, 28, 28, 12).setBoxOutlineColour(Color.DARK_GRAY.getRGB()).setDigitsOnly(true);
		r.setMaxStringLength(3);
		r.setText(String.valueOf(colour.red));
		r.setDigitsOnly(true);
		fieldList.add(r);

		g = new SonarTextField(3, fontRenderer, 74, 28, 28, 12).setBoxOutlineColour(Color.DARK_GRAY.getRGB()).setDigitsOnly(true);
		g.setMaxStringLength(3);
		g.setText(String.valueOf(colour.green));
		g.setDigitsOnly(true);
		fieldList.add(g);

		b = new SonarTextField(4, fontRenderer, 130, 28, 28, 12).setBoxOutlineColour(Color.DARK_GRAY.getRGB()).setDigitsOnly(true);
		b.setMaxStringLength(3);
		b.setText(String.valueOf(colour.blue));
		b.setDigitsOnly(true);
		fieldList.add(b);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		drawRect(5, 5, xSize - 5, 20, configured);
	}

	@Override
	public void onTextFieldChanged(SonarTextField field) {
		super.onTextFieldChanged(field);
		configured = FontHelper.getIntFromColor(r.getIntegerFromText(), g.getIntegerFromText(), b.getIntegerFromText());
		//reset();
	}

	@Override
	protected void keyTyped(char c, int i) throws IOException {
		super.keyTyped(c, i);
		if (isCloseKey(i)) {
			setCurrentColourAndSaveLast(configured);
		}
	}
}
