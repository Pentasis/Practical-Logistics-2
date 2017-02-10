package sonar.logistics;

import java.util.LinkedHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.utils.SimpleProfiler;
import sonar.logistics.api.connecting.INetworkCache;
import sonar.logistics.api.wireless.IEntityTransceiver;
import sonar.logistics.connections.managers.EmitterManager;

public class LogisticsEvents {

	public static final int saveDimension = 0;

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.side == Side.CLIENT) {
			return;
		}
		if (event.phase == Phase.END) {
			//SimpleProfiler.start("nets");
			Logistics.getNetworkManager().tick();
			Logistics.getServerManager().onServerTick();
			Logistics.getNetworkManager().updateEmitters = false;
			EmitterManager.tick(); // this must happen at the end, since the dirty boolean will be changed and will upset tiles
			Logistics.getDisplayManager().tick();
			//System.out.println("nets: " + SimpleProfiler.finish("nets")/1000.0);
		}
	}

	@SubscribeEvent
	public void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		Logistics.getServerManager().sendFullPacket(event.player);
		Logistics.getServerManager().requireUpdates.add((EntityPlayer) event.player);
	}

	@SubscribeEvent
	public void onLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		Logistics.getServerManager().sendFullPacket(event.player);
		Logistics.getServerManager().requireUpdates.add((EntityPlayer) event.player);
	}

	@SubscribeEvent
	public void onLoggedIn(EntityEvent.EnteringChunk event) {
		if (event.getEntity() != null && !event.getEntity().getEntityWorld().isRemote && event.getEntity() instanceof EntityPlayer) {
			Logistics.getServerManager().requireUpdates.add((EntityPlayer) event.getEntity());
		}
	}

	@SubscribeEvent
	public void onEntityTransceiverClicked(PlayerInteractEvent.EntityInteract event) {
		if (event.getSide().isServer() && event.getItemStack() != null && event.getItemStack().getItem() instanceof IEntityTransceiver) {
			((IEntityTransceiver) event.getItemStack().getItem()).onRightClickEntity(event.getEntityPlayer(), event.getItemStack(), event.getEntity());
		}
	}
}