package thomasb.web.dispatch;

import java.util.UUID;


public interface HandlerRegistry {

	boolean containsKey(Object id);
	
	RequestHandler get(Object id);
	
	RequestHandler put(UUID id, RequestHandler handler);
	
	RequestHandler putIfAbsent(UUID id, RequestHandler handler);
	
	boolean replace(UUID id, RequestHandler oldHandler, RequestHandler newHandler);
	
	RequestHandler remove(Object id);
}
