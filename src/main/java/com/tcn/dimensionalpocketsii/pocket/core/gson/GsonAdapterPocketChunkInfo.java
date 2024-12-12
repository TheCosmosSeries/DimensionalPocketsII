package com.tcn.dimensionalpocketsii.pocket.core.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.nbt.CosmosNBTHelper.Const;

public class GsonAdapterPocketChunkInfo implements JsonSerializer<PocketChunkInfo>, JsonDeserializer<PocketChunkInfo> {

	public GsonAdapterPocketChunkInfo() { }

	@Override
	public PocketChunkInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		JsonObject pos = object.getAsJsonObject("pos");
		
		boolean singleChunk = object.get("single_chunk").getAsBoolean();
		
		int x = pos.get(Const.NBT_POS_X_KEY).getAsInt();
		int z = pos.get(Const.NBT_POS_Z_KEY).getAsInt();
		
		return new PocketChunkInfo(new CosmosChunkPos(x, z), singleChunk);
	}

	@Override
	public JsonElement serialize(PocketChunkInfo src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		JsonObject pos = new JsonObject();
		
		object.addProperty("single_chunk", src.isSingleChunk());
		
		pos.addProperty(Const.NBT_POS_X_KEY, src.getDominantChunk().getX());
		pos.addProperty(Const.NBT_POS_Z_KEY, src.getDominantChunk().getZ());
		
		object.add("pos", pos);
		
		return object;
	}
}