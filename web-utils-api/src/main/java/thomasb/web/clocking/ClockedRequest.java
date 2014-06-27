package thomasb.web.clocking;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ClockedRequest<T> {
	public static final String TIME_PARAMETER = "time_count";
	public static final String DATA_PARAMETER = "data";
	
	HttpServletRequest getRequest();
	
	HttpServletResponse getResponse();
	
	T getData();
	
	int getTime();
	
}
