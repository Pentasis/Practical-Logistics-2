package sonar.logistics.api.wireless;

import java.util.ArrayList;

import sonar.logistics.api.cabling.ILogicTile;

/** implemented on the Data Receiver */
public interface IDataReceiver extends ILogicTile {

	/** gets the network ID of all currently connected networks */
	public ArrayList<Integer> getConnectedNetworks();

	/** rechecks connected Data Emitters to ensure the connected network IDs are correct. Typically triggered by an alert {@link RefreshType} */
	public void refreshConnectedNetworks();

}
