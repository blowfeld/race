package thomasb.race.app.handlers;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonValue;

import thomasb.web.handler.HandlerContext;
import thomasb.web.handler.RequestHandler;

public class RedirectUtil {
	static void setHandler(HandlerContext context, UUID current, RequestHandler successor) {
		JsonValue currentId = jsonStringOf(current);
		JsonValue redirectId = successor != null ? jsonStringOf(successor.getId()) : JsonValue.NULL;
		context.setResponseParameter("handler", currentId);
		context.setResponseParameter("redirect", redirectId);
	}
	
	private static JsonValue jsonStringOf(UUID uuid) {
		return Json.createArrayBuilder().add(uuid.toString()).build().get(0);
	}
}
