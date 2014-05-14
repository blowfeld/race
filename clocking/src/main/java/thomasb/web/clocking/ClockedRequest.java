package thomasb.web.clocking;

import javax.servlet.AsyncContext;

final class ClockedRequest<T> {
	private final AsyncContext context;
	private final T data;
	private final int requestTime;
	
	ClockedRequest(AsyncContext context, T data, int requestTime) {
		this.context = context;
		this.data = data;
		this.requestTime = requestTime;
	}
	
	AsyncContext getContext() {
		return context;
	}
	
	T getData() {
		return data;
	}
	
	int getTime() {
		return requestTime;
	}
}
