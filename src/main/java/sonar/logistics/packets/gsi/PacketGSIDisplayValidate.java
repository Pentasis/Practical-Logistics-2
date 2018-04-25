package sonar.logistics.packets.gsi;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.api.displays.DisplayGSI;
import sonar.logistics.api.tiles.displays.IDisplay;
import sonar.logistics.common.multiparts.displays.TileDisplayScreen;
import sonar.logistics.networking.ClientInfoHandler;

public class PacketGSIDisplayValidate implements IMessage {

	public NBTTagCompound SAVE_TAG;
	public int DISPLAY_ID = -1;
	public int GSI_IDENTITY = -1;

	public PacketGSIDisplayValidate() {}

	public PacketGSIDisplayValidate(DisplayGSI gsi, IDisplay display) {
		GSI_IDENTITY = gsi.getDisplayGSIIdentity();
		SAVE_TAG = gsi.writeData(new NBTTagCompound(), SyncType.SAVE);
		DISPLAY_ID = display.getIdentity();	
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		GSI_IDENTITY = buf.readInt();
		SAVE_TAG = ByteBufUtils.readTag(buf);
		DISPLAY_ID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(GSI_IDENTITY);
		ByteBufUtils.writeTag(buf, SAVE_TAG);
		buf.writeInt(DISPLAY_ID);
	}

	public static class Handler implements IMessageHandler<PacketGSIDisplayValidate, IMessage> {

		@Override
		public IMessage onMessage(PacketGSIDisplayValidate message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
				if (player != null) {
					SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> doMessage(message, ctx));
				}
			}
			return null;
		}

		public static void doMessage(PacketGSIDisplayValidate message, MessageContext ctx) {
			IDisplay display = ClientInfoHandler.instance().getConnectedDisplay(message.DISPLAY_ID);
			DisplayGSI gsi = display.getGSI();
			gsi.readData(message.SAVE_TAG, SyncType.SAVE);
			gsi.validate();

		}

	}

}
