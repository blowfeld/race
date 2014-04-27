package thomasb.web.clocking;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface ClockedRequestProcessor {
	void service(ServletRequest request, ServletResponse response);
	void timeoutResponse(int time, ServletResponse response);
}
