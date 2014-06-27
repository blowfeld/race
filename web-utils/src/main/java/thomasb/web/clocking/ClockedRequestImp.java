package thomasb.web.clocking;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class ClockedRequestImp<T> implements ClockedRequest<T> {
	private final AsyncContext context;
	private final T data;
	private final int requestTime;
	
	ClockedRequestImp(AsyncContext context) {
		this(context, null);
	}
	
	ClockedRequestImp(AsyncContext context, T data) {
		this(context, data, readTime(context));
	}
	
	ClockedRequestImp(AsyncContext context, T data, int requestTime) {
		this.context = context;
		this.data = data;
		this.requestTime = requestTime;
	}
	
	private static int readTime(AsyncContext request) {
		HttpServletRequest httpRequest = (HttpServletRequest)request.getRequest();
		
		return Integer.valueOf(httpRequest.getParameter(TIME_PARAMETER));
	}
	
	@Override
	public HttpServletRequest getRequest() {
		return (HttpServletRequest) context.getRequest();
	}
	
	
	@Override
	public HttpServletResponse getResponse() {
		return (HttpServletResponse) context.getResponse();
	}
	
	@Override
	public T getData() {
		return data;
	}
	
	@Override
	public int getTime() {
		return requestTime;
	}
	
	AsyncContext getContext() {
		return context;
	}
	
	ClockedRequestImp<T> withData(T data) {
		return new ClockedRequestImp<T>(context, data, requestTime);
	}
}
