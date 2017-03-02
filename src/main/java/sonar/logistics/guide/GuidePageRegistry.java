package sonar.logistics.guide;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;

public class GuidePageRegistry {

	public static ArrayList<IGuidePage> pages = new ArrayList<IGuidePage>();

	public static void init() {
		// addGuidePage(new Welcome(0));
		addGuidePage(new GeneralPage(0, "guide.Welcome.title", "guide.Welcome.name"));
		addGuidePage(new ExamplesPages(1));
		addGuidePage(new OperatorPage(6));
		addGuidePage(new ForgingHammerPage(7));
		addGuidePage(new SapphireOrePage(8));
		addGuidePage(new SapphirePage(9));
		addGuidePage(new SapphireDustPage(10));
		addGuidePage(new StonePlatePage(11));
		addGuidePage(new CablePage(12));
		addGuidePage(new NodePage(13));
		addGuidePage(new ArrayPage(14));
		addGuidePage(new TransferNodePage(15));
		addGuidePage(new TransceiverPage(16));
		addGuidePage(new EntityTransceiverPage(17));
		addGuidePage(new LargeDisplayScreenPage(18));
		addGuidePage(new DisplayScreenPage(19));
		addGuidePage(new DataEmitterPage(20));
		addGuidePage(new DataReceiverPage(21));
		addGuidePage(new RedstoneSignallerPage(22));
		addGuidePage(new InfoReaderPage(23));
		addGuidePage(new InventoryReaderPage(24));
		addGuidePage(new FluidReaderPage(25));
		addGuidePage(new EnergyReaderPage(26));

	}

	public static void addGuidePage(IGuidePage page) {
		pages.add(page);
	}

	public static IGuidePage getGuidePage(ItemStack stack) {
		for (IGuidePage page : pages) {
			if (page instanceof BaseItemPage && ItemStack.areItemsEqual(stack, ((BaseItemPage) page).getItemStack())) {
				return page;
			}
		}
		return null;
	}

	public static IGuidePage getGuidePage(int pageID) {
		for (IGuidePage page : pages) {
			if (page.pageID() == pageID) {
				return page;
			}
		}
		return null;
	}

	public static IGuidePage getGuidePage(String unlocalizedName) {
		for (IGuidePage page : pages) {
			if (page instanceof BaseItemPage && unlocalizedName.equals(((BaseItemPage) page).unlocalizedName.substring(5))) {
				return page;
			}
		}
		return null;
	}
}