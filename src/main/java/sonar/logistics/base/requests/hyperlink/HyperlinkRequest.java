package sonar.logistics.base.requests.hyperlink;

import net.minecraft.client.gui.GuiScreen;
import sonar.logistics.core.tiles.displays.info.elements.base.IHyperlinkRequirement;

public class HyperlinkRequest implements IHyperlinkRequirement {

	public GuiScreen screen;
	public String hyperlink;
	
	public HyperlinkRequest(GuiScreen screen){
		this(screen, "");
	}
	
	public HyperlinkRequest(GuiScreen screen, String hyperlink){
		this.screen = screen;
		this.hyperlink = hyperlink;
	}

	@Override
	public String getHyperlink() {
		return hyperlink;
	}

	@Override
	public void onGuiClosed(String hyperlink) {
		if(screen instanceof IHyperlinkRequirementGui){
			((IHyperlinkRequirementGui) screen).onHyperlinkRequirementCompleted(hyperlink);
		}
	}
	
}
