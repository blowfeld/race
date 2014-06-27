package thomasb.web.clocking;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

final class RequestCollection<T> {
	private static final Dispatcher DISPATCHER = new Dispatcher();
	private static final Function<ClockedRequestImp<?>, AsyncContext> TO_CONTEXT = new Function<ClockedRequestImp<?>, AsyncContext>() {
		@Override
		public AsyncContext apply(ClockedRequestImp<?> request) {
			return request.getContext();
		}
	};
	
	private final List<ClockedRequestImp<T>> requests = newArrayList();
	private final ClockedRequestProcessor<T> requestProcessor;
	private final Collection<String> participants;
	
	RequestCollection(ClockedRequestProcessor<T> requestProcessor, Collection<String> participants) {
		this.requestProcessor = requestProcessor;
		this.participants = ImmutableSet.copyOf(participants);
	}
	
	boolean add(ClockedRequestImp<T> request, int currentTime) throws IOException, ServletException {
		if (!participants.contains(request.getRequest().getSession().getId())) {
			throw new UnknownParticipantException(request.getRequest().getSession().getId());
		}
		
		if (currentTime > request.getTime()) {
			DISPATCHER.submit(timeout(request, currentTime).getContext());
			return false;
		}
		
		requests.add(request);
		
		return requests.size() == participants.size();
	}
	
	void submit() {
		writeResponseData();
		DISPATCHER.submit(transform(requests, TO_CONTEXT));
		requests.clear();
	}
	
	private ClockedRequestImp<T> timeout(ClockedRequestImp<T> request, int currentTime)
			throws ServletException, IOException {
		AsyncContext context = request.getContext();
		int requestTime = request.getTime();
		
		JsonStructure timeoutData = requestProcessor.timeoutResponse(context, requestTime, currentTime);
		writeResponse(request, timeoutData);
		
		return request;
	}
	
	private void writeResponseData() {
		List<? extends JsonStructure> responseData = requestProcessor.process(requests);
		for (int i = 0; i < requests.size(); i++) {
			ClockedRequest<T> request = requests.get(i);
			try {
				writeResponse(request, responseData.get(i));
			} catch (IOException e) {
				//ignore or log
			}
		}
	}
	
	private void writeResponse(ClockedRequest<T> request, JsonStructure data) throws IOException {
		HttpServletResponse response = request.getResponse();
		response.setContentType("application/json");
		PrintWriter responseWriter = response.getWriter();
		
		JsonObjectBuilder responseObject = Json.createObjectBuilder();
		responseObject.add(ClockedRequest.TIME_PARAMETER, request.getTime());
		responseObject.add(ClockedRequest.DATA_PARAMETER, data);
		responseWriter.write(responseObject.build().toString());
	}
	
	/**
	 * Indicate that a request is received from an unregistered session id.
	 */
	public static class UnknownParticipantException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public UnknownParticipantException(String id) {
			super("Id " + id + " is not registered.");
		}
	}
}
