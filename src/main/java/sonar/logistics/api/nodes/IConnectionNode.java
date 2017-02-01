package sonar.logistics.api.nodes;

import java.util.ArrayList;

import sonar.logistics.api.cabling.ILogicTile;

/** implemented by Nodes, provides a list of all the blocks they are connected to, normally just one, but can be more */
public interface IConnectionNode extends ILogicTile {

	/** adds any available connections to the current Map
	 * @param connections the current list of Entries */
	public void addConnections(ArrayList<NodeConnection> connections);
	
	public int getPriority();
}
