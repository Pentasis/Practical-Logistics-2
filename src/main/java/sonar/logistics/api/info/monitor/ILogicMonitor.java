package sonar.logistics.api.info.monitor;

import java.util.ArrayList;
import java.util.UUID;

import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IChannelledTile;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.connections.monitoring.MonitoredList;

/***/
public interface ILogicMonitor<T extends IMonitorInfo> extends IChannelledTile {
	
	/**identifies this monitor, this will be synced across the server and client*/	
	///**the coords selected by the player to be monitored*/
	//public IdentifiedCoordsList getMonitoringCoords();
	
	/**the instance of the MonitorHandler this LogicMonitor uses*/
	public LogicMonitorHandler<T> getHandler();	
	
	/**gets a list of all the players viewing*/
	
	public void sentViewerPacket(MonitorViewer viewer, boolean sentFirstPacket);
	
	/**this is when the list should be set and added to the ClinetMonitoredLists*/
	public MonitoredList<T> sortMonitoredList(MonitoredList<T> updateInfo);
	
	public void setMonitoredInfo(MonitoredList<T> updateInfo);
	
	public ChannelType channelType();
	
	public IMonitorInfo getMonitorInfo(int pos);
	
	public int getMaxInfo();
	
	public INetworkCache getNetwork();
	
}
