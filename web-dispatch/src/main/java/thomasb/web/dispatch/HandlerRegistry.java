package thomasb.web.dispatch;


public interface HandlerRegistry {

	boolean containsKey(Object id);
	
	RequestHandler get(Object id);
	
	RequestHandler put(String id, RequestHandler handler);
	
	RequestHandler putIfAbsent(String id, RequestHandler handler);
	
	boolean replace(String id, RequestHandler oldHandler, RequestHandler newHandler);
	
	RequestHandler remove(Object id);
}
