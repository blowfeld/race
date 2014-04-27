package thomasb.web.clocking;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

class TestAsyncContext implements AsyncContext {
	private long invocationTime;
	
	@Override
	public void complete() {
		invocationTime = System.currentTimeMillis();
	}

	long getInvocationTime() {
		return invocationTime;
	}

	@Override
	public ServletRequest getRequest() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ServletResponse getResponse() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasOriginalRequestAndResponse() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void dispatch() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void dispatch(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void dispatch(ServletContext context, String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void start(Runnable run) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addListener(AsyncListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addListener(AsyncListener listener,
			ServletRequest servletRequest, ServletResponse servletResponse) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends AsyncListener> T createListener(Class<T> clazz)
			throws ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setTimeout(long timeout) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}
}