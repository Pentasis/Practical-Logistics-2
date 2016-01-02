package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.data.TileProvider;

public class VanillaTileEntityProvider extends TileProvider {

	public static String name = "Vanilla-Tile Helper";
	public String[] categories = new String[] { "SPECIAL"};
	public String[] subcategories = new String[] {"Burn Time","Current Time","Cook Time","Current Fuel","Current Note"};

	@Override
	public String helperName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		if (target != null) {
			if (target instanceof TileEntityFurnace) {
				return true;
			}
			if (target instanceof TileEntityNote) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		byte id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target != null) {
			if (target instanceof TileEntityFurnace) {
				TileEntityFurnace furnace = (TileEntityFurnace) target;
				infoList.add(new StandardInfo(id, 0, 0, furnace.furnaceBurnTime));
				infoList.add(new StandardInfo(id, 0, 1, furnace.furnaceCookTime, "ticks"));
				infoList.add(new StandardInfo(id, 0, 2, 200, "ticks"));
				infoList.add(new StandardInfo(id, 0, 3, furnace.currentItemBurnTime));
			}
			if (target instanceof TileEntityNote) {
				TileEntityNote noteBlock = (TileEntityNote) target;
				infoList.add(new StandardInfo(id, 0, 4, noteBlock.note));
			}
		}

	}

	@Override
	public String getCategory(byte id) {
		return categories[id];
	}

	@Override
	public String getSubCategory(byte id) {
		return subcategories[id];
	}
}