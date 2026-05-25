package lotrpickupfilter.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.LinkedHashSet;
import java.util.Set;

public class PickupFilterData {
	public static final String FILTER_TAG = "LOTRPickupFilter";
	private static final String ENTRIES_TAG = "Entries";

	public static String getKey(ItemStack stack) {
		if (stack == null || stack.getItem() == null) {
			return "";
		}
		String name = Item.itemRegistry.getNameForObject(stack.getItem());
		return name + "@" + stack.getItemDamage();
	}

	public static boolean matches(EntityPlayer player, ItemStack stack) {
		return getFilter(player).contains(getKey(stack));
	}

	public static Set<String> getFilter(EntityPlayer player) {
		NBTTagCompound persisted = getPersistedData(player);
		return readFilter(persisted.getCompoundTag(FILTER_TAG));
	}

	public static void setFilter(EntityPlayer player, Set<String> filter) {
		NBTTagCompound persisted = getPersistedData(player);
		persisted.setTag(FILTER_TAG, writeFilter(filter));
	}

	public static NBTTagCompound writeFilter(Set<String> filter) {
		NBTTagCompound data = new NBTTagCompound();
		NBTTagList entries = new NBTTagList();
		for (String key : filter) {
			entries.appendTag(new NBTTagString(key));
		}
		data.setTag(ENTRIES_TAG, entries);
		return data;
	}

	public static Set<String> readFilter(NBTTagCompound data) {
		Set<String> filter = new LinkedHashSet<String>();
		NBTTagList entries = data.getTagList(ENTRIES_TAG, 8);
		for (int i = 0; i < entries.tagCount(); i++) {
			filter.add(entries.getStringTagAt(i));
		}
		return filter;
	}

	public static ItemStack stackFromKey(String key) {
		if (key == null) {
			return null;
		}
		int split = key.lastIndexOf('@');
		if (split <= 0 || split >= key.length() - 1) {
			return null;
		}
		Item item = (Item) Item.itemRegistry.getObject(key.substring(0, split));
		if (item == null) {
			return null;
		}
		try {
			return new ItemStack(item, 1, Integer.parseInt(key.substring(split + 1)));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static NBTTagCompound getPersistedData(EntityPlayer player) {
		NBTTagCompound data = player.getEntityData();
		if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
			data.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
		}
		return data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
	}
}
