package sonar.logistics.api.readers;

import java.util.ArrayList;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncCoords;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncUUID;
import sonar.core.utils.IUUIDIdentity;
import sonar.logistics.api.wireless.ClientDataEmitter;

/**used when syncing Logic Monitors for display in the Display Screen with the client, since some may not be loaded on client side.*/
public class ClientLogicReader implements IUUIDIdentity, INBTSyncable {

	public ArrayList<ISyncPart> syncParts = new ArrayList<ISyncPart>();
	public SyncUUID uuid = new SyncUUID(0);
	public SyncCoords coords = new SyncCoords(1);
	public SyncTagType.INT maxInfo = new SyncTagType.INT(2);
	{
		syncParts.addAll(Lists.newArrayList(uuid, coords, maxInfo));
	}

	public ClientLogicReader() {}

	public ClientLogicReader(IInfoProvider emitter) {
		this.uuid.setObject(emitter.getIdentity());
		this.coords.setCoords(emitter.getCoords());
		this.maxInfo.setObject(emitter.getMaxInfo());
	}

	public ClientLogicReader(UUID uuid, BlockCoords coords, int maxInfo) {
		this.uuid.setObject(uuid);
		this.coords.setCoords(coords);
		this.maxInfo.setObject(maxInfo);
	}

	public ClientLogicReader copy() {
		return new ClientLogicReader(uuid.getUUID(), coords.getCoords(), maxInfo.getObject());
	}

	@Override
	public UUID getIdentity() {
		return uuid.getUUID();
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.readSyncParts(nbt, type, syncParts);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.writeSyncParts(nbt, type, syncParts, type.isType(SyncType.SAVE));
		return nbt;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ClientDataEmitter) {
			return getIdentity().equals(((IUUIDIdentity) obj).getIdentity()) && coords.getCoords().equals(((ClientDataEmitter) obj).coords.getCoords());
		}
		return false;
	}

	public int hashCode() {
		return getIdentity().hashCode();
	}

}
