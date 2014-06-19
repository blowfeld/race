package thomasb.web.clocking;

import static java.lang.Thread.sleep;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

class TestAsyncContext implements AsyncContext {
	private long invocationTime;
	private final ClockedSubmission<?> clockedSubmission;
	private final AsyncContext followUpRequest;
	private final int intervalCount;
	private final int delay;
	private final HttpServletResponse response;
	private final String sessionId;
	
	TestAsyncContext(ClockedSubmission<?> clockedSubmission,
			int intervalCount,
			int delay,
			AsyncContext followUpRequest,
			String sessionId) throws IOException {
		this.clockedSubmission = clockedSubmission;
		this.intervalCount = intervalCount;
		this.delay = delay;
		this.followUpRequest = followUpRequest;
		this.sessionId = sessionId;
		this.response = createResponseMock();
	}
	
	TestAsyncContext(int intervalCount, String sessionId) throws IOException {
		this(null, intervalCount, 0, null, sessionId);
	}

	@Override
	public void complete() {
		invocationTime = System.currentTimeMillis();
		if (clockedSubmission != null) {
			try {
				sleep(delay);
				clockedSubmission.addRequest(followUpRequest);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	long getInvocationTime() {
		return invocationTime;
	}

	@Override
	public ServletRequest getRequest() {
		HttpSession sessionMock = mock(HttpSession.class);
		when(sessionMock.getId()).thenReturn(sessionId);
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		doReturn(String.valueOf(intervalCount))
				.when(request).getParameter(ClockedRequestHandlerImp.TIME_PARAMETER);
		doReturn(sessionMock).when(request).getSession();
		
		return request;
	}

	@Override
	public ServletResponse getResponse() {
		return response;
	}
	
	private HttpServletResponse createResponseMock() throws IOException {
		HttpServletResponse mock = mock(HttpServletResponse.class);
		
		PrintWriter writerMock = mock(PrintWriter.class);
		doReturn(writerMock ).when(mock).getWriter();
		
		return mock;
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