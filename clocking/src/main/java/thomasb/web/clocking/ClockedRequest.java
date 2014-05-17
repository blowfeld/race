package thomasb.web.clocking;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class ClockedRequest<T> {
	public static final String TIME_PARAMETER = "time_count";
	public static final String DATA_PARAMETER = "data";
	
	private final AsyncContext context;
	private final T data;
	private final int requestTime;
	
	ClockedRequest(AsyncContext context) {
		this(context, null);
	}
	
	ClockedRequest(AsyncContext context, T data) {
		this(context, data, readTime(context));
	}
	
	ClockedRequest(AsyncContext context, T data, int requestTime) {
		this.context = context;
		this.data = data;
		this.requestTime = requestTime;
	}
	
	private static int readTime(AsyncContext request) {
		HttpServletRequest httpRequest = (HttpServletRequest)request.getRequest();
		
		return Integer.valueOf(httpRequest.getParameter(TIME_PARAMETER));
	}
	
	public HttpServletRequest getRequest() {
		return (HttpServletRequest) context.getRequest();
	}
	
	
	public HttpServletResponse getResponse() {
		return (HttpServletResponse) context.getResponse();
	}
	
	public T getData() {
		return data;
	}
	
	public int getTime() {
		return requestTime;
	}
	
	AsyncContext getContext() {
		return context;
	}
	
	ClockedRequest<T> withData(T data) {
		return new ClockedRequest<T>(context, data, requestTime);
	}
}
