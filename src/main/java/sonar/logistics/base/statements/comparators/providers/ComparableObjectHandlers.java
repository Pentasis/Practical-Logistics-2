package sonar.logistics.base.statements.comparators.providers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import org.apache.commons.lang3.reflect.FieldUtils;
import sonar.logistics.api.core.tiles.displays.info.comparators.IComparableProvider;
import sonar.logistics.api.core.tiles.displays.info.comparators.LogicIdentifier;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ComparableObjectHandlers {

	public static class ItemStackLogic implements IComparableProvider<ItemStack> {

		@Override
		public void getComparableObjects(String parent, ItemStack obj, Map<LogicIdentifier, Object> objects) {
			objects.put(new LogicIdentifier(parent, "items"), obj.getItem());
			objects.put(new LogicIdentifier(parent, "stackSize"), obj.getCount());
		}

	}

	public static class NBTTagCompoundLogic implements IComparableProvider<NBTTagCompound> {

		public List<Integer> accepted = Lists.newArrayList(NBT.TAG_BYTE, NBT.TAG_SHORT, NBT.TAG_INT, NBT.TAG_LONG, NBT.TAG_FLOAT, NBT.TAG_DOUBLE);

		@Override
		public void getComparableObjects(String parent, NBTTagCompound tag, Map<LogicIdentifier, Object> objects) {
			Map<String, NBTBase> tagMap;
			try {
				tagMap = (Map<String, NBTBase>) FieldUtils.readField(tag, "tagMap", true);
			} catch (IllegalAccessException e) {
				tagMap = Maps.newHashMap();
			}
			int tagCount = 0;
			for (Entry<String, NBTBase> entry : tagMap.entrySet()) {
				if (accepted.contains((int) entry.getValue().getId())) {
					if (entry.getValue().getId() == NBT.TAG_COMPOUND) {
						tagCount++;
						getComparableObjects("tag " + tagCount, tag, objects);
					} else {
						try {
							objects.put(new LogicIdentifier(parent, entry.getKey()), FieldUtils.readField(entry.getValue(), "data", true));
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
