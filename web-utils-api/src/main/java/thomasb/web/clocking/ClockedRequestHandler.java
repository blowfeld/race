package thomasb.web.clocking;

import thomasb.web.handler.RequestHandler;

/**
 * A {@code ClockedRequestHandler} synchronizes between request of its participants.
 * 
 * The participants are supposed to send requests in fixed time intervals. For
 * each time interval the responses to the participants are delayed until the
 * request of the last participant arrives or a timeout limit is reached.
 * 
 * Participants must specify the count of the intended time interval in their
 * request and must not send a request to the succeeding interval before they
 * received the response from the server or a given timeout is reached.
 */
public interface ClockedRequestHandler extends RequestHandler {
	//tagging interface
}