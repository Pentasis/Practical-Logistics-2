package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.Info;
import cpw.mods.fml.common.network.ByteBufUtils;

public class CategoryInfo extends Info<CategoryInfo> {

	public String category;

	public static CategoryInfo createInfo(String category) {
		CategoryInfo info = new CategoryInfo();
		info.category = category;
		return info;
	}

	@Override
	public String getName() {
		return "Header";
	}

	@Override
	public byte getProviderID() {
		return -1;
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public String getSubCategory() {
		return category;
	}

	@Override
	public String getData() {
		return category;
	}

	@Override
	public String getDisplayableData() {
		return getData();
	}

	@Override
	public int getDataType() {
		return 1;
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		category = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, category);
	}

	public void readFromNBT(NBTTagCompound tag) {
		this.category = tag.getString("category");
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setString("category", category);
	}

	/*
	 * @Override public boolean isEqualType(Info info) { if (info != null &&
	 * info.getName() == this.getName()) { return
	 * info.getCategory().equals(category); } return false; }
	 * 
	 * @Override public void emptyData() {
	 * 
	 * }
	 */
	@Override
	public CategoryInfo instance() {
		return new CategoryInfo();
	}

	@Override
	public void writeUpdate(CategoryInfo currentInfo, NBTTagCompound tag) {
		if (!currentInfo.category.equals(category)) {
			tag.setString("c", category);
			this.category = currentInfo.category;
		}

	}

	@Override
	public void readUpdate(NBTTagCompound tag) {
		if (tag.hasKey("c")) {
			this.category = tag.getString("c");
		}
	}

	@Override
	public boolean matches(CategoryInfo currentInfo) {
		return currentInfo.category.equals(category);
	}

}
