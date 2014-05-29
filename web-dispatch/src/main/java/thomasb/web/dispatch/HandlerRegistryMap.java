package thomasb.web.dispatch;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("serial")
class HandlerRegistryMap extends ConcurrentHashMap<UUID, RequestHandler> implements HandlerRegistry {
	//Reuse ConcurrentHashMap
}