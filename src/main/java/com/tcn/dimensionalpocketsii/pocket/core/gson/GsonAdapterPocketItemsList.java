package com.tcn.dimensionalpocketsii.pocket.core.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.tcn.dimensionalpocketsii.ModReferences;
import com.tcn.dimensionalpocketsii.DimensionalPockets;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class GsonAdapterPocketItemsList implements JsonSerializer<NonNullList<ItemStack>>, JsonDeserializer<NonNullList<ItemStack>> {

	@Override
	public NonNullList<ItemStack> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		
		JsonPrimitive object = null;
		
		try {
			object = json.getAsJsonPrimitive();
		} catch (IllegalStateException e) {
			DimensionalPockets.CONSOLE.warning("ItemList Legacy Check FAILED!");
		}
		
		if (object != null) {
			String string = object.getAsString();
			CompoundTag compoundOut = new CompoundTag();
			
			try {
				compoundOut = TagParser.parseTag(string);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			int size = compoundOut.getInt("size");
			NonNullList<ItemStack> list = NonNullList.<ItemStack>withSize(size, ItemStack.EMPTY);
			ContainerHelper.loadAllItems(compoundOut, list, ServerLifecycleHooks.getCurrentServer().registryAccess());
			
			return list;
		} else {
			DimensionalPockets.CONSOLE.info("Item list returned NULL. Implementing new ItemList.");
			NonNullList<ItemStack> newList = NonNullList.<ItemStack>withSize(ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE, ItemStack.EMPTY);
			
			return newList;
		}
	}

	@Override
	public JsonElement serialize(NonNullList<ItemStack> src, Type typeOfSrc, JsonSerializationContext context) {
		CompoundTag compound = new CompoundTag();
		ContainerHelper.saveAllItems(compound, src, ServerLifecycleHooks.getCurrentServer().registryAccess());
		compound.putInt("size", src.size());
		
		String nbt_string = compound.toString();
		
		return new JsonPrimitive(nbt_string);
	}

}
