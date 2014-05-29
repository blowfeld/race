package thomasb.web.dispatch;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestHandler {
	
	void handle(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException;
	
	UUID getId();
	
}
