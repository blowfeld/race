package thomasb.web.clocking;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ClockedRequest<T> {
	
	HttpServletRequest getRequest();
	
	HttpServletResponse getResponse();
	
	T getData();
	
	int getTime();
	
}
