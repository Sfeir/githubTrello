package com.sfeir.githubTrello;

import java.io.IOException;
import java.util.Collection;

import org.codehaus.jackson.map.ObjectMapper;

import static com.google.common.collect.Lists.*;

import static java.util.Collections.*;
import static org.apache.commons.lang3.ArrayUtils.*;

public class Json {

	public static JsonToType fromJsons(Collection<String> jsons) {
		return new JsonToType(jsons.toArray(EMPTY_STRING_ARRAY));
	}

	public static JsonToType fromJson(String json) {
		return new JsonToType(json);
	}

	public static class JsonToType {
		public JsonToType(String... jsons) {
			this.jsons = jsons;
			this.mapper = new ObjectMapper();
		}

		public <T> Collection<T> to(Class<T> type) {
			try {
				Collection<T> collection = newArrayList();
				for (String json : jsons) {
					Collection<T> objects = mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(Collection.class, type));
					collection.addAll(objects);
					return collection;
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			return emptyList();
		}

		private ObjectMapper mapper;

		private String[] jsons;
	}

	public static TypeToJson fromType(Object object) {
		return new TypeToJson(object);
	}


	public static class TypeToJson {
		public TypeToJson(Object objects) {
			this.objects = objects;
			this.mapper = new ObjectMapper();
		}

		public String toJson()
		{
			try {
				return mapper.writeValueAsString(objects);
			}
			catch (IOException e) {
				e.printStackTrace();//TODO:Logger
			}
			return "";
		}

		private Object objects;
		private ObjectMapper mapper;
	}
}
