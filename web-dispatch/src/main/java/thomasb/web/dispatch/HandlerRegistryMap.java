package thomasb.web.dispatch;

import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("serial")
class HandlerRegistryMap extends ConcurrentHashMap<String, RequestHandler> implements HandlerRegistry {
	//Reuse ConcurrentHashMap
}