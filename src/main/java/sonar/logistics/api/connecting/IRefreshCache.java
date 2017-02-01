package sonar.logistics.api.connecting;

/** implemented on {@link INetworkCache}s which should be updated on every world tick */
public interface IRefreshCache extends INetworkCache {

	/**called when a new block is added/removed to the network, or if the last network ID doesn't match the current ID, or if connections have changed
	 * this should be used to update caches with relevant data
	 * @param networkID the latest NetworkID of this network 
	 * @param refresh should all network blocks be reconfigured? NOTE: if false Data Cables won't update their connections!*/
	public void refreshCache(int networkID, RefreshType refresh);

	/**called on every World Tick
	 * this should be used to update caches with relevant data, that needs to be constantly updated e.g. Items, Fluids, Energy etc
	 * @param networkID the latest NetworkID of this network */	 
	public void updateNetwork(int networkID);
}

