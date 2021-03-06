package sonar.logistics.core.items.guide;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;
import sonar.core.common.item.SonarItem;

import javax.annotation.Nonnull;

public class ItemGuide extends SonarItem implements IFlexibleGui<ItemStack> {

	@Nonnull
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote) {
			SonarCore.instance.guiHandler.openBasicItemStack(false, stack, player, world, player.getPosition(), 0);
		}
		return new ActionResult(EnumActionResult.SUCCESS, stack);

	}

	//// GUI \\\\

	@Override
	public Object getServerElement(ItemStack obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return id == 0 ? new ContainerGuide(player) : null;
	}

	@Override
	public Object getClientElement(ItemStack obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return id == 0 ? new GuiGuide(player) : null;
	}

	@Override
	public void onGuiOpened(ItemStack obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {}

}
