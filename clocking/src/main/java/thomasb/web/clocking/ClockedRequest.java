package thomasb.web.clocking;

import javax.servlet.AsyncContext;

public final class ClockedRequest<T> {
	private final AsyncContext context;
	private final T data;
	private final int requestTime;
	
	public ClockedRequest(AsyncContext context, T data, int requestTime) {
		this.context = context;
		this.data = data;
		this.requestTime = requestTime;
	}
	
	public AsyncContext getContext() {
		return context;
	}
	
	public T getData() {
		return data;
	}
	
	public int getTime() {
		return requestTime;
	}
}
