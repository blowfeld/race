package thomasb.web.dispatch;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Maps.newHashMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

@RunWith(MockitoJUnitRunner.class)
public class DispatchHandlerTest {
	private static final String SESSION_ID_1 = "1";
	private static final String SESSION_ID_2 = "2";
	
	@Mock HttpServletResponse response;
	@Mock HttpServletRequest request_1_1;
	@Mock HttpServletRequest request_1_2;
	@Mock HttpSession session_1;

	@Mock HttpServletRequest request_2_1;
	@Mock HttpServletRequest request_2_2;
	@Mock HttpSession session_2;

	@Mock RequestHandler handler_1_1;
	@Mock RequestHandler handler_1_2;
	@Mock RequestHandler handler_2_1;
	@Mock RequestHandler handler_2_2;
	
	private DispatchHandler dispatchHandler;
	private ListMultimap<String, RequestHandler> handlers;

	@Before
	public void setupRequest() {
		when(session_1.getId()).thenReturn(SESSION_ID_1);
		when(request_1_1.getSession()).thenReturn(session_1);
		when(request_1_2.getSession()).thenReturn(session_1);

		when(session_2.getId()).thenReturn(SESSION_ID_2);
		when(request_2_1.getSession()).thenReturn(session_2);
		when(request_2_2.getSession()).thenReturn(session_2);
	}
	
	@Before
	public void setupHandlers() {
		handlers = ImmutableListMultimap.of(
				"1", handler_1_1,
				"1", handler_1_2,
				"2", handler_2_1,
				"2", handler_2_2);
	}
	
	@Before
	public void setupDispatchHandler() {
		dispatchHandler = new TestDispatchHandler();
	}
	
	@Test
	public void newSessionIsRegistered() throws ServletException, IOException {
		dispatchHandler.handle(request_1_1, response);
		
		assertEquals(handler_1_1, dispatchHandler.getRegistry().get(SESSION_ID_1));
	}

	@Test
	public void requestIsHandled() throws ServletException, IOException {
		dispatchHandler.handle(request_1_1, response);
		
		verify(handler_1_1).handle(request_1_1, response);
	}
	
	@Test
	public void mulitpleSessionsAndRequests() throws ServletException, IOException {
		dispatchHandler.handle(request_1_1, response);
		dispatchHandler.handle(request_2_1, response);
		dispatchHandler.handle(request_1_2, response);
		dispatchHandler.handle(request_2_2, response);
		
		assertEquals(handler_1_1, dispatchHandler.getRegistry().get(SESSION_ID_1));
		assertEquals(handler_2_1, dispatchHandler.getRegistry().get(SESSION_ID_2));
		
		verify(handler_1_1).handle(request_1_1, response);
		verify(handler_1_1).handle(request_1_2, response);
		verify(handler_2_1).handle(request_2_1, response);
		verify(handler_2_1).handle(request_2_2, response);
	}
	
	@Test
	public void handlerNotReplacedForConcurrentFirstRequestsFromSameSession() throws ServletException, IOException, InterruptedException {
		CountDownLatch latch_1 = new CountDownLatch(1);
		final CountDownLatch latch_3 = new CountDownLatch(2);
		
		final DispatchHandler dispatcher = new TestDispatchHandler(latch_1, latch_3);
		
		Thread other = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					dispatcher.handle(request_1_1, response); // This call finishes first
					latch_3.countDown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		other.start();
		latch_1.await();
		
		dispatcher.handle(request_1_2, response);
		
		other.join();
		
		assertEquals(handler_1_1, dispatcher.getRegistry().get(SESSION_ID_1));
		
		verify(handler_1_1).handle(request_1_1, response);
		verify(handler_1_1).handle(request_1_2, response);
	}
	
	private class TestDispatchHandler extends DispatchHandler {
		private final CountDownLatch latch_1;
		private final CountDownLatch latch_2 = new CountDownLatch(0);
		private final CountDownLatch latch_3;

		Map<String, Boolean> assigned = newHashMap(of("1", false, "2", false));
		
		TestDispatchHandler() {
			this(new CountDownLatch(0), new CountDownLatch(0));
		}
		
		TestDispatchHandler(CountDownLatch latch_1, CountDownLatch latch_3) {
			this.latch_1 = latch_1;
			this.latch_3 = latch_3;
		}
		
		@Override
		protected RequestHandler assignHandler(String id) {
			latch_2.countDown();
			
			synchronized (this) {
				latch_1.countDown(); //release other thread
				
				try {
					latch_2.await(); // Wait until other thread enters assignHandler
					if (latch_3.getCount() == 1L) {
						latch_3.await(); // other thread waits until this releases lock_3
					}
					latch_3.countDown();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				
				switch (id) {
					case "1":
						return assign("1");
					case "2":
						return assign("2");
				}
			}
			
			throw new IllegalArgumentException("id must be \"1\" or \"2\"");
		}

		private RequestHandler assign(String id) {
			List<RequestHandler> handlersForId = handlers.get(id);
			if (assigned.get(id)) {
				return handlersForId.get(1);
			}
			
			assigned.put(id, true);
			
			return handlersForId.get(0);
		}
	}
}
