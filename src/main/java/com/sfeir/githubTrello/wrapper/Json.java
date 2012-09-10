package com.sfeir.githubTrello.wrapper;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import static java.util.Collections.*;

public final class Json {
	public static <T> T fromJsonToObject(String json, Class<T> type) {
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

	public static <T> Collection<T> fromJsonToObjects(String json, Class<T> type) {
		try {
			return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(Collection.class, type));
		}
		catch (IOException e) {
			logger.info(e, e);
			return emptyList();
		}
	}

	public static String fromObjectToJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		}
		catch (IOException e) {
			logger.error(e, e);
			return "";
		}
	}

	public static String extractValue(String json, String... attributes) {
		JsonNode node = fromJsonToObject(json, JsonNode.class);
		for (String attribute : attributes) {
			node = node.isArray() ? fromMultipleNodes(attribute, ((ArrayNode) node)) : node.get(attribute);
		}
		return node.getTextValue();
	}


	private static JsonNode fromMultipleNodes(String attribute, ArrayNode node) {
		Matcher matcher = Pattern.compile("(.*)\\[(.*)=(.*)\\]").matcher(attribute);
		if (!matcher.matches()) {
			logger.warn("Json node with multiple elements with attribute: " + attribute);
		}
		String actualAttribute = matcher.group(1);
		String key = matcher.group(2);
		String value = matcher.group(3);

		Iterator<JsonNode> elements = node.getElements();
		while (elements.hasNext())
		{
			JsonNode element = elements.next();
			if (value.equals(element.get(key).getTextValue())) {
				return element.get(actualAttribute);
			}
		}
		return node;



		//		JsonNode actualNode = selectUnique(nodeElements, having(on(JsonNode.class).get(key).getTextValue().equals(value)));
		//		return actualNode.get(actualAttribute);
	}

	private Json() {}

	private static final ObjectMapper mapper = new ObjectMapper();
	private static final Log logger = LogFactory.getLog(Json.class);
}
