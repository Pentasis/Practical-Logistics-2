package sonar.logistics.integration.ae2;

import appeng.api.storage.ICellInventory;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.IMEInventoryHandler;
import sonar.core.network.sync.SyncTagType;
import sonar.logistics.api.asm.ASMInfo;
import sonar.logistics.api.core.tiles.displays.info.IComparableInfo;
import sonar.logistics.api.core.tiles.displays.info.IInfo;
import sonar.logistics.api.core.tiles.displays.info.INameableInfo;
import sonar.logistics.api.core.tiles.displays.info.IProvidableInfo;
import sonar.logistics.api.core.tiles.displays.info.comparators.ComparableObject;
import sonar.logistics.api.core.tiles.displays.info.register.LogicPath;
import sonar.logistics.api.core.tiles.displays.info.register.RegistryType;
import sonar.logistics.core.tiles.displays.info.types.BaseInfo;

import java.util.List;

@ASMInfo(id = AE2DriveInfo.id, modid = "appliedenergistics2")
public class AE2DriveInfo extends BaseInfo<AE2DriveInfo> implements IProvidableInfo<AE2DriveInfo>,INameableInfo<AE2DriveInfo>, IComparableInfo<AE2DriveInfo> {

	public static final String id = "ae2-drive-info";
	public SyncTagType.LONG totalBytes = new SyncTagType.LONG(1);
	public SyncTagType.LONG usedBytes = new SyncTagType.LONG(2);
	public SyncTagType.LONG totalTypes = new SyncTagType.LONG(3);
	public SyncTagType.LONG usedTypes = new SyncTagType.LONG(4);
	public SyncTagType.LONG itemCount = new SyncTagType.LONG(5);
	public SyncTagType.INT driveNum = new SyncTagType.INT(5);
	{
		syncList.addParts(totalBytes, usedBytes, totalTypes, usedTypes, itemCount, driveNum);
	}

	public AE2DriveInfo() {}
	
	public AE2DriveInfo(int driveNum){
		this.driveNum.setObject(driveNum);
	}
	
	public AE2DriveInfo(List<IMEInventoryHandler> invHandlers, int driveNum) {
		this(driveNum);
		for (IMEInventoryHandler handler : invHandlers) {
			if (handler instanceof ICellInventoryHandler) {
				ICellInventoryHandler cell = (ICellInventoryHandler) handler;
				ICellInventory cellInventory = cell.getCellInv();
				if (cellInventory != null) {
					totalBytes.current += cellInventory.getTotalBytes();
					usedBytes.current += cellInventory.getUsedBytes();
					totalTypes.current += cellInventory.getTotalItemTypes();
					usedTypes.current += cellInventory.getStoredItemTypes();
					itemCount.current += cellInventory.getStoredItemCount();
				}
			}
		}
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public boolean isIdenticalInfo(AE2DriveInfo info) {
		return (info.totalBytes.getObject().equals(totalBytes.getObject())) && (info.usedBytes.getObject().equals(usedBytes.getObject())) && (info.totalTypes.getObject().equals(totalTypes.getObject())) && (info.usedTypes.getObject().equals(usedTypes.getObject())) && (info.itemCount.getObject().equals(itemCount.getObject()));
	}

	@Override
	public boolean isMatchingInfo(AE2DriveInfo info) {
		return info.driveNum.getObject().equals(driveNum.getObject());
	}

	@Override
	public boolean isMatchingType(IInfo info) {
		return info instanceof AE2DriveInfo;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public AE2DriveInfo copy() {
		AE2DriveInfo newInfo = new AE2DriveInfo();
		newInfo.syncList.copyFrom(syncList);
		newInfo.setPath(getPath().dupe());
		return newInfo;
	}

	@Override
	public List<ComparableObject> getComparableObjects(List<ComparableObject> objects) {
		objects.add(new ComparableObject(this, "total bytes", totalBytes.getObject()));
		objects.add(new ComparableObject(this, "used bytes", usedBytes.getObject()));
		objects.add(new ComparableObject(this, "total types", totalTypes.getObject()));
		objects.add(new ComparableObject(this, "used types", usedTypes.getObject()));
		objects.add(new ComparableObject(this, "items count", itemCount.getObject()));
		objects.add(new ComparableObject(this, "drive number", driveNum.getObject()));
		return objects;
	}

	@Override
	public String getClientIdentifier() {
		return driveNum.getObject()==0 ? "ME DRIVE" :"ME CELL " + driveNum.getObject();
	}

	@Override
	public String getClientObject() {
		return "Bytes[" + usedBytes.getObject() +  "/" + totalBytes.getObject() + "]";
	}

	@Override
	public String getClientType() {
		return driveNum.getObject()==0 ?"ME drive" :"ME cell";
	}

	@Override
	public RegistryType getRegistryType() {
		return RegistryType.TILE;
	}

	@Override
	public AE2DriveInfo setRegistryType(RegistryType type) {
		return this;
	}

	@Override
	public void setFromReturn(LogicPath path, Object returned) {
		//custom handlers
	}
}
