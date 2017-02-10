package sonar.logistics.common.multiparts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.netty.buffer.ByteBuf;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import sonar.core.api.IFlexibleGui;
import sonar.core.integration.multipart.SonarMultipartHelper;
import sonar.core.network.sync.SyncNBTAbstract;
import sonar.core.network.sync.SyncNBTAbstractList;
import sonar.core.network.sync.SyncUUID;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.Logistics;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.connecting.INetworkCache;
import sonar.logistics.api.connecting.RefreshType;
import sonar.logistics.api.wireless.ClientDataEmitter;
import sonar.logistics.api.wireless.IDataEmitter;
import sonar.logistics.api.wireless.IDataReceiver;
import sonar.logistics.client.gui.GuiDataReceiver;
import sonar.logistics.common.containers.ContainerDataReceiver;
import sonar.logistics.connections.managers.EmitterManager;
import sonar.logistics.helpers.LogisticsHelper;

public class DataReceiverPart extends SidedMultipart implements IDataReceiver, IFlexibleGui, IByteBufTile {

	public SyncNBTAbstractList<ClientDataEmitter> clientEmitters = new SyncNBTAbstractList<ClientDataEmitter>(ClientDataEmitter.class, 2);
	public SyncUUID playerUUID = new SyncUUID(3);
	public SyncNBTAbstract<ClientDataEmitter> selectedEmitter = new SyncNBTAbstract<ClientDataEmitter>(ClientDataEmitter.class, 4);
	// public ArrayList<IDataEmitter> emitters = new ArrayList();
	public ArrayList<Integer> networks = new ArrayList();
	{
		syncList.addParts(clientEmitters, playerUUID, selectedEmitter);
	}

	public DataReceiverPart() {
		super(0.0625 * 5, 0.0625 / 2, 0.0625 * 4);
	}

	public DataReceiverPart(EntityPlayer player, EnumFacing dir) {
		super(dir, 0.0625 * 5, 0.0625 / 2, 0.0625 * 4);
		playerUUID.setObject(player.getGameProfile().getId());
	}

	public void update() {
		super.update();
		if (isClient()) {
			return;
		}
	}

	public void refreshConnectedNetworks() {
		networks = getNetworks();
	}

	public ArrayList<Integer> getNetworks() {
		ArrayList<Integer> networks = new ArrayList();
		ArrayList<IDataEmitter> emitters = getEmitters();
		for (IDataEmitter emitter : emitters) {
			if (emitter.getNetworkID() != -1) {
				networks.add(emitter.getNetworkID());
			}
		}
		return networks;
	}

	public ArrayList<IDataEmitter> getEmitters() {
		ArrayList<IDataEmitter> emitters = new ArrayList();
		for (ClientDataEmitter dataEmitter : clientEmitters.getObjects()) {
			IDataEmitter emitter = EmitterManager.getEmitter(dataEmitter.getIdentity());
			if (emitter != null && emitter.canPlayerConnect(playerUUID.getUUID())) {
				emitters.add(emitter);
				emitter.connect(this);
			}
		}
		return emitters;
	}

	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, PartMOP hit) {
		if (!LogisticsHelper.isPlayerUsingOperator(player)) {
			if (!getWorld().isRemote) {
				openFlexibleGui(player, 0);
			}
			return true;
		}
		return false;
	}

	public void setLocalNetworkCache(INetworkCache network) {
		super.setLocalNetworkCache(network);
		if (isServer())
			network.markDirty(RefreshType.FULL);
	}

	@Override
	public ArrayList<Integer> getConnectedNetworks() {
		return networks;
	}

	/* @Override public EnumSet<PartSlot> getSlotMask() { return getOccludedSlots(); }
	 * @Override public EnumSet<PartSlot> getOccludedSlots() { return EnumSet.of(PartSlot.getFaceSlot(face)); } */
	@Override
	public Object getServerElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return id == 0 ? new ContainerDataReceiver(this) : null;
	}

	@Override
	public Object getClientElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return id == 0 ? new GuiDataReceiver(this) : null;
	}

	@Override
	public void onGuiOpened(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch(id){
		case 0:
			SonarMultipartHelper.sendMultipartSyncToPlayer(this, (EntityPlayerMP) player);
			EmitterManager.addViewer(player);
			break;
		}		
	}

	@Override
	public void onFirstTick() {
		super.onFirstTick();
		if (this.isServer()) {
			Logistics.getNetworkManager().updateEmitters = true;
			network.markDirty(RefreshType.FULL);
		}
	}

	public void onRemoved() {
		getEmitters().forEach(emitter -> emitter.disconnect(this));
		super.onRemoved();
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			selectedEmitter.writeToBuf(buf);
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			selectedEmitter.readFromBuf(buf);
			ClientDataEmitter emitter = selectedEmitter.getObject().copy();
			ArrayList<ClientDataEmitter> emitters = (ArrayList<ClientDataEmitter>) clientEmitters.getObjects().clone();
			Iterator<ClientDataEmitter> iterator = emitters.iterator();
			boolean found = false;
			while (iterator.hasNext()) {
				ClientDataEmitter entry = iterator.next();
				if (entry.equals(emitter)) {// FIXME what's going on here then
					IDataEmitter tile = EmitterManager.getEmitter(entry.getIdentity());
					tile.disconnect(this);
					iterator.remove();
					found = true;
					break;
				}
			}
			if (!found) {
				IDataEmitter tile = EmitterManager.getEmitter(emitter.getIdentity());
				tile.connect(this);
				emitters.add(emitter);
			}

			clientEmitters.setObjects(emitters);
			networks = getNetworks();
			network.markDirty(RefreshType.FULL);
			sendSyncPacket();
			break;
		}
	}

	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		super.addSelectionBoxes(list);
		/* double p = 0.0625; double height = p * 12, width = p * 8, length = p * 14; switch (face) { case EAST: list.add(new AxisAlignedBB(1, 0, (width) / 2, 1 - length, height, 1 - width / 2)); break; case NORTH: list.add(new AxisAlignedBB((width) / 2, 0, length, 1 - width / 2, height, 0)); break; case SOUTH: list.add(new AxisAlignedBB((width) / 2, 0, 1, 1 - width / 2, height, 1 - length)); break; case WEST: list.add(new AxisAlignedBB(length, 0, (width) / 2, 0, height, 1 - width / 2)); break; default: break; } */
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.partReceiver);
	}
}
