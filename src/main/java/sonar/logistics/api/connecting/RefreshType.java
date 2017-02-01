package sonar.logistics.api.connecting;

/** not fully implemented yet, will be used to make refreshing networks more efficient by removing the need to refresh everything everytime */
public enum RefreshType {
	/** when a cable is changed */
	FULL,
	/** when an emitter is changed */
	CONNECTED_NETWORKS,
	ALERT,
	/** when a node is changed */
	CONNECTED_BLOCKS,
	NONE;

	public boolean shouldRefreshCables() {
		return this == FULL;
	}

	public boolean shouldRefreshNetworks() {
		return this == FULL || this == CONNECTED_NETWORKS || this == ALERT;
	}

	public boolean shouldRefreshConnections() {
		return this == FULL || this == CONNECTED_BLOCKS || this == CONNECTED_NETWORKS || this == ALERT;
	}

	public boolean shouldAlertWatchingNetworks() {
		return this != ALERT && this != NONE;
	}
}
