package thomasb.web.dispatch;

import java.io.IOException;

import javax.json.JsonValue;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HandlerContext {
	HttpServletRequest getRequest();
	HttpServletResponse getResponse();
	
	void setResponseParameter(String name, JsonValue value);
	void writeResponse() throws IOException;
}
