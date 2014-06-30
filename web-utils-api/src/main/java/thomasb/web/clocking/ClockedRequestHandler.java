package thomasb.web.clocking;

import thomasb.web.handler.RequestHandler;

/**
 * A {@code ClockedRequestHandler} synchronizes between request of its participants.
 * 
 * The participants are supposed to send requests in fixed time intervals. For
 * each time interval the responses to the participants are delayed until the
 * request of the last participant arrives or a timeout limit is reached.
 * 
 * The response to the first request received by each participant is a JSON object
 * the interval containing the interval length, the timeout length and initial
 * as provided by the {@link ClockedRequestProcessor} in the 'interval', 'timeout'
 * and 'data' properties.
 * 
 * Participants must specify the count of the intended time interval in their
 * request and must not send a request to the succeeding interval before they
 * received the response from the server or a given timeout is reached.
 * 
 * The data transfered with a request must be a JSON object and contain the
 * time interval in the 'time_count' property. Further properties can be present
 * in the request data.
 * 
 */
public interface ClockedRequestHandler extends RequestHandler {
	static final String TIME_PARAMETER = "time_count";
	static final String INTERVAL_PARAMETER = "interval";
	static final String TIMEOUT_PARAMETER = "timeout";
	static final String DATA_PARAMETER = "data";
	
	//tagging interface
}