package sonar.logistics.api.tiles;

import java.util.UUID;

import net.minecraft.util.EnumFacing;
import sonar.core.utils.IWorldPosition;
import sonar.logistics.api.networks.ILogisticsNetwork;
import sonar.logistics.api.networks.INetworkListener;
import sonar.logistics.api.states.TileMessage;
import sonar.logistics.api.tiles.cable.CableRenderType;
import sonar.logistics.api.tiles.cable.NetworkConnectionType;


/** used to be LogicTile. implemented by Tile Entities which can connect to Data Cables */
public interface INetworkTile extends IWorldPosition, INetworkListener, INetworkConnection {

	/** gets the network cache's ID */
	public int getNetworkID();
	
	/**the currently connected network*/
	public ILogisticsNetwork getNetwork();
	
	/**this tiles identity on the network*/
	public int getIdentity();		
	
	public TileMessage[] getValidMessages();
}
