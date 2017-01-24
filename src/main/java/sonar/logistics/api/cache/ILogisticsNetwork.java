package sonar.logistics.api.cache;

import java.util.Map;

import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.core.utils.Pair;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.connections.monitoring.MonitoredList;

/** implemented on {@link INetworkCache}s which can monitor info, items, fluids etc */
public interface ILogisticsNetwork extends INetworkCache {
	
	public RefreshType getLastRefresh();
	
	/** called when a display is connected to the network */
	public void addDisplay(IInfoDisplay display);

	/** called when a display is disconnected from the network */
	public void removeDisplay(IInfoDisplay display);

	/** called when a {@link ILogicMonitor} is connected to the network */
	public <T extends IMonitorInfo> void addMonitor(ILogicMonitor<T> monitor);

	/** called when a {@link ILogicMonitor} is disconnected to the network */
	public <T extends IMonitorInfo> void removeMonitor(ILogicMonitor<T> monitor);

	/** gathers the monitored list required by the {@link ILogicMonitor} which is then cached 
	 * @param connections TODO
	 * @return TODO*/
	public <T extends IMonitorInfo> MonitoredList<T> updateMonitoredList(ILogicMonitor<T> monitor, int infoID, Map<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> connections);

	/**gets the full monitored list for the Handler type
	 * @param type the type of handler to get a list for
	 * @return a full list of data
	 */
	public <T extends IMonitorInfo> Map<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> getMonitoredList(LogicMonitorHandler<T> type);

	//public MonitoredList<?> getListForMonitor(ILogicMonitor monitor);
	
}
