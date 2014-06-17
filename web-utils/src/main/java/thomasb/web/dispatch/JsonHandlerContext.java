package thomasb.web.dispatch;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thomasb.web.handler.HandlerContext;

public class JsonHandlerContext implements HandlerContext {
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final Map<String, JsonValue> responseParameters = new HashMap<>();

	public JsonHandlerContext(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}
	
	@Override
	public HttpServletRequest getRequest() {
		return request;
	}

	@Override
	public HttpServletResponse getResponse() {
		return response;
	}

	@Override
	public void setResponseParameter(String name, JsonValue value) {
		responseParameters.put(name, value);
	}

	@Override
	public void writeResponse() throws IOException {
		JsonGenerator responseGenerator = Json.createGenerator(response.getWriter());
		responseGenerator.writeStartObject();
		for (Entry<String, JsonValue> parameter : responseParameters.entrySet()) {
			responseGenerator.write(parameter.getKey(), parameter.getValue());
		}
		responseGenerator.writeEnd();
		responseGenerator.close();
	}
}
