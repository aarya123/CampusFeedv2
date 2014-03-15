package com.purdue.CampusFeed.API;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonHelper {

	public static Gson createCampusFeedGson() {
		return new GsonBuilder()
		.registerTypeAdapter(Date.class, new DateTypeAdapter())
		.create();
	}
	
	private static class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

		@Override
		public Date deserialize(JsonElement jsonElement, Type type,
				JsonDeserializationContext context) throws JsonParseException {
			return new Date(jsonElement.getAsLong());
		}

		@Override
		public JsonElement serialize(Date date, Type type,
				JsonSerializationContext context) {
			return new JsonPrimitive(date.getTime());
		}
		
	}
}
