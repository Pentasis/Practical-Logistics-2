package sonar.logistics.network;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.PL2;
import sonar.logistics.api.wireless.ClientDataEmitter;

public class PacketClientEmitters implements IMessage {

	public ArrayList<ClientDataEmitter> emitters;

	public PacketClientEmitters() {
	}

	public PacketClientEmitters(ArrayList<ClientDataEmitter> emitters) {
		this.emitters = emitters;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		emitters = new ArrayList();
		if (tag.hasKey("emitters")) {
			NBTTagList tagList = tag.getTagList("emitters", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < tagList.tagCount(); i++) {
				emitters.add(NBTHelper.instanceNBTSyncable(ClientDataEmitter.class, tagList.getCompoundTagAt(i)));
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList tagList = new NBTTagList();
		emitters.forEach(emitter -> tagList.appendTag(emitter.writeData(new NBTTagCompound(), SyncType.SAVE)));
		if (!tagList.hasNoTags()) {
			tag.setTag("emitters", tagList);
		}
		ByteBufUtils.writeTag(buf, tag);

	}

	public static class Handler implements IMessageHandler<PacketClientEmitters, IMessage> {
		@Override
		public IMessage onMessage(PacketClientEmitters message, MessageContext ctx) {
			SonarCore.proxy.getThreadListener(ctx).addScheduledTask(new Runnable() {
				public void run() {
					PL2.getClientManager().clientEmitters = message.emitters;
				}
			});
			return null;
		}
	}

}
