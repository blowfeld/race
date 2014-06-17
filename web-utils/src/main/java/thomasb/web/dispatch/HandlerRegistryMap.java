package thomasb.web.dispatch;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import thomasb.web.handler.RequestHandler;

@SuppressWarnings("serial")
class HandlerRegistryMap extends ConcurrentHashMap<UUID, RequestHandler> implements HandlerRegistry {
	//Reuse ConcurrentHashMap
}