package sonar.logistics.api.cabling;

import sonar.core.utils.IWorldPosition;
import sonar.logistics.api.connecting.INetworkCache;
import sonar.logistics.api.render.ICableRenderer;

/** implemented on Tile Entities and Forge Multipart parts which are cables */
public interface IDataCable extends ICableRenderer, IWorldPosition, INetworkConnectable {
	
	/** the cable should check it's connections and see if it is connected to the correct ones */
	public void refreshConnections();
	
	/**ensures all connections are connected to the same network*/
	public void configureConnections(INetworkCache network);	
	
	public boolean hasConnections();

	/** the {@link BlockCoords} this Block/FMP Part should be registered as on the Network
	 * @return the {@link BlockCoords} */
	
}
