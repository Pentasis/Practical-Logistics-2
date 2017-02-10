package sonar.logistics.common.multiparts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mcmultipart.MCMultiPartMod;
import mcmultipart.multipart.INormallyOccludingPart;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.PartSlot;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.SyncEnum;
import sonar.core.utils.Pair;

public abstract class FacingMultipart extends LogisticsMultipart implements INormallyOccludingPart {

	// public EnumFacing face;
	public SyncEnum<EnumFacing> face = new SyncEnum(EnumFacing.values(), -2);
	{
		syncList.addPart(face);
	}
	public FacingMultipart() {
		super();
	}

	public FacingMultipart(EnumFacing dir) {
		super();
		this.face.setObject(dir);
	}

	public EnumFacing getFacing() {
		return face.getObject();
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		addSelectionBoxes(list);
	}

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		ArrayList<AxisAlignedBB> boxes = new ArrayList();
		addSelectionBoxes(boxes);
		boxes.forEach(box -> {
			if (box.intersectsWith(mask)) {
				list.add(box);
			}
		});
	}

	@Override
	public boolean rotatePart(EnumFacing axis) {
		Pair<Boolean, EnumFacing> rotate = rotatePart(getFacing(), axis);
		if (rotate.a) {
			if (getContainer().getPartInSlot(PartSlot.getFaceSlot(rotate.b)) != null) {
				return false;
			}
			if (isServer()) {
				UUID uuid = getUUID();
				BlockPos pos = getPos();
				World world = getWorld();
				getContainer().removePart(this);
				face.setObject(rotate.b);
				firstTick = false;
				MultipartHelper.addPart(world, pos, this, uuid);
				sendUpdatePacket(true);
			}
		}
		return rotate.a;
	}

	@Override
	public EnumFacing[] getValidRotations() {
		return EnumFacing.HORIZONTALS;
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound tag, SyncType type) {
		super.writeData(tag, type);
		// tag.setByte("face", (byte) face.ordinal());
		return tag;
	}

	@Override
	public void readData(NBTTagCompound tag, SyncType type) {
		super.readData(tag, type);
		// face = EnumFacing.VALUES[tag.getByte("face")];
	}

	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
		super.writeUpdatePacket(buf);
		face.writeToBuf(buf);
	}

	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		super.readUpdatePacket(buf);
		face.readFromBuf(buf);
	}

	@Override
	public ConnectionType canConnect(EnumFacing dir) {
		return ConnectionType.NETWORK;
	}

	@Override
	public ItemStack getItemStack() {
		return null;
	}

	@Override
	public IBlockState getActualState(IBlockState state) {
		World w = getContainer().getWorldIn();
		BlockPos pos = getContainer().getPosIn();
		return state.withProperty(ORIENTATION, getFacing());
	}

	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(MCMultiPartMod.multipart, new IProperty[] { ORIENTATION });
	}

}
