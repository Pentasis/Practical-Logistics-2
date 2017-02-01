package sonar.logistics.connections.monitoring;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import sonar.core.api.StorageSize;
import sonar.core.utils.Pair;
import sonar.logistics.api.info.IJoinableInfo;
import sonar.logistics.api.info.IMonitorInfo;

public class MonitoredList<T extends IMonitorInfo> extends ArrayList<T> {

	// public MonitoredList<T> EMPTY = newMonitoredList();
	// public ArrayList<T> info = new ArrayList<T>();
	public ArrayList<T> changed = new ArrayList<T>();
	public ArrayList<T> removed = new ArrayList<T>();
	public StorageSize sizing;
	public boolean hasChanged = true;
	public int networkID;// embedded network ID to wipe lists from the wrong network on client side, avoiding empty lists being sent and preventing monitors displaying old data

	public MonitoredList(int networkID) {
		super();
		this.networkID = networkID;
	}

	public MonitoredList(int networkID, ArrayList<T> items, StorageSize sizing, ArrayList<T> changed, ArrayList<T> removed) {
		super(items);
		this.networkID = networkID;
		this.sizing = sizing;
		this.changed = changed;
		this.removed = removed;
	}

	public static <I extends IMonitorInfo> MonitoredList<I> newMonitoredList(int networkID) {
		return new MonitoredList<I>(networkID, Lists.<I>newArrayList(), new StorageSize(0, 0), Lists.<I>newArrayList(), Lists.<I>newArrayList());
	}

	public ArrayList<T> cloneInfo() {
		return (ArrayList<T>) super.clone();
	}

	public void setInfo(ArrayList<T> info) {
		this.clear();
		this.addAll(info);
	}

	public MonitoredList<T> copyInfo() {
		return new MonitoredList<T>(networkID, (ArrayList<T>) cloneInfo(), new StorageSize(sizing.getStored(), sizing.getMaxStored()), (ArrayList<T>) changed.clone(), (ArrayList<T>) removed.clone());
	}
	
	public T findInfoInList(T newInfo, MonitoredList<T> previousList){
		for(T lastInfo : previousList){
			if(lastInfo.isIdenticalInfo(newInfo)){
				return lastInfo;
			}
		}
		return null;
	}

	public void addInfoToList(T newInfo, MonitoredList<T> previousList) {			
		T lastInfo = findInfoInList(newInfo, previousList);
		if(lastInfo == null){
			
		}		
		if (newInfo instanceof IJoinableInfo) {
			for (int i = 0; i < this.size(); i++) {
				T storedInfo = this.get(i);
				if (((IJoinableInfo) storedInfo).canJoinInfo(newInfo)) {
					set(i, (T) ((IJoinableInfo) storedInfo).joinInfo(newInfo.copy()));
					return;
				}
			}
		}
		add((T) newInfo.copy());
	}

	public MonitoredList<T> updateList(MonitoredList<T> lastList) {
		ArrayList<T> changed = ((ArrayList<T>) cloneInfo());
		ArrayList<T> removed = ((ArrayList<T>) lastList.cloneInfo());
		
		
		((ArrayList<T>) lastList.cloneInfo()).forEach(last -> 
			forEach(current -> {				
				if (last.isMatchingInfo(current)) {
					removed.remove(last);
				}
				if(last.isIdenticalInfo(current)){
					changed.remove(current);
				}				
		}));
		this.changed = changed;
		this.removed = removed;
		hasChanged = !changed.isEmpty() || !removed.isEmpty();
		if(this.hasChanged){
			this.changed = changed;
		}		
		return this;
	}

	public void markDirty() {
		hasChanged = true;
	}

	/** @param info the info type you wish to check
	 * @return a boolean for if the info was changed and the new info */
	public Pair<Boolean, IMonitorInfo> getLatestInfo(IMonitorInfo oldInfo) {
		for (T newInfo : this) {
			if (newInfo.isMatchingType(oldInfo) && newInfo.isMatchingInfo(oldInfo) && !newInfo.isIdenticalInfo(oldInfo)) {
				return new Pair(true, newInfo);
			}
		}
		return new Pair(false, oldInfo);
	}
}