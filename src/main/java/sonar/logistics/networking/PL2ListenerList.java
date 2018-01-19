package sonar.logistics.networking;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.ChunkHelper;
import sonar.core.listener.ISonarListenable;
import sonar.core.listener.ListenableList;
import sonar.core.listener.ListenerList;
import sonar.core.listener.ListenerTally;
import sonar.core.listener.PlayerListener;
import sonar.logistics.api.networks.ILogisticsNetwork;
import sonar.logistics.api.states.TileMessage;
import sonar.logistics.api.tiles.cable.CableRenderType;
import sonar.logistics.api.tiles.cable.NetworkConnectionType;
import sonar.logistics.api.tiles.displays.IDisplay;
import sonar.logistics.api.viewers.ILogicListenable;
import sonar.logistics.api.viewers.ListenerType;
import sonar.logistics.networking.connections.ChunkViewerHandler;

public class PL2ListenerList extends ListenableList<PlayerListener> {

	public ListenerList<IDisplay> displayListeners = new ListenerList<IDisplay>(1);

	public PL2ListenerList(ISonarListenable<PlayerListener> listen, int maxTypes) {
		super(listen, maxTypes);
	}

	public List<PlayerListener> getAllListeners(ListenerType... enums) {
		List<PlayerListener> listeners = super.getListeners(enums);
		for (ListenerType type : enums) {
			if (type == ListenerType.LISTENER) {
				List<IDisplay> displays = displayListeners.getListeners(0);
				List<EntityPlayerMP> players = ChunkViewerHandler.instance().getWatchingPlayers(displays);
				players.forEach(player -> {
					PlayerListener listener = new PlayerListener(player);
					if (!listeners.contains(listener)) {
						listeners.add(listener);
					}
				});
			}
		}
		return listeners;
	}
	

	public boolean hasListeners() {
		boolean hasListeners = super.hasListeners();
		if (!hasListeners) {
			List<IDisplay> displays = displayListeners.getListeners(0);
			for (IDisplay display : displays) {
				List<EntityPlayerMP> players = ChunkViewerHandler.instance().getWatchingPlayers(display);
				if (!players.isEmpty()) {
					return true;
				}
			}
		}
		return hasListeners;
	}

	public ListenerList<IDisplay> getDisplayListeners() {
		return displayListeners;
	}

}
