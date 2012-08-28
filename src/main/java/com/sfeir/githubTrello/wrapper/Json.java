package com.sfeir.githubTrello.wrapper;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

import static java.util.Collections.*;

public final class Json {

	public static JsonToType fromJson(String json) {
		return new JsonToType(json);
	}

	public static class JsonToType {
		public JsonToType(String json) {
			this.json = json;
			this.mapper = new ObjectMapper();
		}

		public <T> T toObject(Class<T> type) {
			try {
				return mapper.readValue(json, type);
			}
			catch (IOException ioe) {
				try {
					return type.newInstance();
				}
				catch (InstantiationException | IllegalAccessException roe) {
					logger.error(ioe, ioe);
					logger.error(roe, roe);
					return null;
				}
			}
		}

		public <T> Collection<T> toCollection(Class<T> type) {
			try {
				return mapper.<Collection<T>> readValue(json, mapper.getTypeFactory().constructCollectionType(Collection.class, type));
			}
			catch (IOException e) {
				logger.info(e, e);
			}
			return emptyList();
		}

		private ObjectMapper mapper;

		private String json;
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
				logger.error(e, e);
				return "";
			}
		}

		private Object objects;
		private ObjectMapper mapper;
	}

	private Json() {}

	private static final Log logger = LogFactory.getLog(Json.class);
}
