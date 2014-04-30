package thomasb.web.clocking;

import static java.lang.Thread.sleep;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class TestAsyncContext implements AsyncContext {
	private long invocationTime;
	private final ClockedSubmissionThread submissionThread;
	private final AsyncContext request;
	private final int intervalCount;
	private final int delay;
	private final HttpServletResponse response;
	
	TestAsyncContext(ClockedSubmissionThread submissionThread,
			int intervalCount,
			int delay,
			AsyncContext request) {
		this.submissionThread = submissionThread;
		this.intervalCount = intervalCount;
		this.delay = delay;
		this.request = request;
		this.response = createResponseMock();
	}
	
	TestAsyncContext(int intervalCount) {
		this(null, intervalCount, 0, null);
	}

	@Override
	public void complete() {
		invocationTime = System.currentTimeMillis();
		if (submissionThread != null) {
			try {
				sleep(delay);
				submissionThread.addRequest(request);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	long getInvocationTime() {
		return invocationTime;
	}

	@Override
	public ServletRequest getRequest() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		doReturn(String.valueOf(intervalCount))
				.when(request).getParameter(ClockedRequestHandler.TIME_PARAMETER);
		
		return request;
	}

	@Override
	public ServletResponse getResponse() {
		return response;
	}
	
	private HttpServletResponse createResponseMock() {
		HttpServletResponse mock = mock(TestHttpServletResponse.class);
		doCallRealMethod().when(mock).setStatus(408);
		doCallRealMethod().when(mock).getStatus();
		
		return mock;
	}

	private static abstract class TestHttpServletResponse implements HttpServletResponse {
		private int status;
		
		@Override
		public void setStatus(int status) {
			this.status = status;
		}
		
		@Override
		public int getStatus() {
			return status;
		}
	}
	
	
	// ---------- unused ------------------
	
	
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
		throw new UnsupportedOperationException();
	}
}