package sonar.logistics.info.types;

import java.util.List;

import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.logistics.PL2;
import sonar.logistics.PL2Constants;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.displays.elements.IDisplayElement;
import sonar.logistics.api.displays.elements.IElementStorageHolder;
import sonar.logistics.api.displays.elements.types.NetworkFluidGridElement;
import sonar.logistics.api.displays.elements.types.NetworkGridElement;
import sonar.logistics.api.displays.elements.types.NetworkItemGridElement;
import sonar.logistics.api.info.IInfo;
import sonar.logistics.api.info.INameableInfo;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.lists.types.AbstractChangeableList;

@LogicInfoType(id = LogicInfoList.id, modid = PL2Constants.MODID)
public class LogicInfoList extends BaseInfo<LogicInfoList> implements INameableInfo<LogicInfoList> {

	public static final String id = "logiclist";
	public final SyncTagType.INT networkID = (INT) new SyncTagType.INT(2).setDefault(-1);
	public SyncTagType.INT identity = new SyncTagType.INT(0);
	public SyncTagType.STRING infoID = new SyncTagType.STRING(1);
	public boolean listChanged = true, wasRefreshed = false;
	{
		syncList.addParts(identity, infoID, networkID);
	}

	public LogicInfoList() {}

	public LogicInfoList(int identity, String infoID, int networkID) {
		this.identity.setObject(identity);
		this.infoID.setObject(infoID);
		this.networkID.setObject(networkID);
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public boolean isIdenticalInfo(LogicInfoList info) {
		return identity.getObject().equals(info.identity.getObject());
	}

	@Override
	public boolean isMatchingInfo(LogicInfoList info) {
		return infoID.getObject().equals(info.infoID.getObject()) && networkID.getObject() == info.networkID.getObject();
	}

	@Override
	public boolean isMatchingType(IInfo info) {
		return info instanceof LogicInfoList;
	}

	@Override
	public boolean isValid() {
		return identity.getObject() != -1;
	}

	@Override
	public LogicInfoList copy() {
		return new LogicInfoList(identity.getObject(), infoID.getObject(), networkID.getObject());
	}

	@Override
	public String getClientIdentifier() {
		return "List: " + infoID.getObject().toLowerCase();
	}

	@Override
	public String getClientObject() {
		return "LIST";
	}

	@Override
	public String getClientType() {
		return "list";
	}

	@Override
	public void createDefaultElements(List<IDisplayElement> toAdd, IElementStorageHolder h, InfoUUID uuid) {
		switch (this.infoID.getObject()) {
		case MonitoredItemStack.id:
			toAdd.add(new NetworkItemGridElement(uuid));
			break;
		case MonitoredFluidStack.id:
			toAdd.add(new NetworkFluidGridElement(uuid));
			break;
		}
	}
}