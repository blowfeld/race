package thomasb.web.clocking;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Function;

final class RequestCollection<T> {
	private static final Dispatcher DISPATCHER = new Dispatcher();
	private static final Function<ClockedRequest<?>, AsyncContext> TO_CONTEXT = new Function<ClockedRequest<?>, AsyncContext>() {
		@Override
		public AsyncContext apply(ClockedRequest<?> request) {
			return request.getContext();
		}
	};
	
	private final List<ClockedRequest<T>> requests = newArrayList();
	private final ClockedRequestProcessor<T> requestProcessor;
	private final int participants;
	
	RequestCollection(ClockedRequestProcessor<T> requestProcessor,
			int participants) {
		this.requestProcessor = requestProcessor;
		this.participants = participants;
	}
	
	boolean add(ClockedRequest<T> request, int currentTime) throws IOException, ServletException {
		if (currentTime > request.getTime()) {
			DISPATCHER.submit(timeout(request).getContext());
			return false;
		}
		
		requests.add(request);
		
		return requests.size() == participants;
	}
	
	void submit() {
		writeResponseData();
		DISPATCHER.submit(transform(requests, TO_CONTEXT));
		requests.clear();
	}
	
	private ClockedRequest<T> timeout(ClockedRequest<T> request)
			throws ServletException, IOException {
		AsyncContext context = request.getContext();
		int requestTime = request.getTime();
		
		JsonStructure timeoutData = requestProcessor.timeoutResponse(context, requestTime);
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
}