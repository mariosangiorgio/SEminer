package it.polimi.masAPI;

import java.util.LinkedHashMap;
import java.util.Map;

public class URLBuilder {
	private Map<String, String> parameters = new LinkedHashMap<String, String>();

	public URLBuilder(String AppID, ResultObjectType resultObjectType) {
		parameters.put("AppId", AppID);
		parameters.put("ResultObjects", resultObjectType.toString());
	}

	public void addFullTextQuery(String query) {
		parameters.put("FullTextQuery", query);
	}

	public void addParameter(String parameter, String value) {
		parameters.put(parameter, value);
	}

	public void addParameter(String parameter, int value) {
		addParameter(parameter, Integer.toString(value));
	}

	public void setLimits(int startIndex, int endIndex) {
		parameters.put("StartIdx", Integer.toString(startIndex));
		parameters.put("EndIdx", Integer.toString(endIndex));
	}

	public String buildParamsUrl() {
		StringBuilder result = new StringBuilder();
		result.append("/json.svc/search?");
		for (String parameterName : parameters.keySet()) {
			result.append(parameterName + "=" + parameters.get(parameterName) + "&");
		}
		return result.substring(0, result.length() - 1).replace(" ", "%20");
	}
}

enum ResultObjectType {
	Organization, Publication, Author;
}