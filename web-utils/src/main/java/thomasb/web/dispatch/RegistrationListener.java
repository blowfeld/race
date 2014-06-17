package thomasb.web.dispatch;

import java.lang.ref.WeakReference;
import java.util.UUID;

import thomasb.web.handler.RequestHandler;

public class RegistrationListener implements HandlerRegistry {
	private final WeakReference<HandlerRegistry> registry;
	
	public RegistrationListener(HandlerRegistry registry) {
		this.registry = new WeakReference<>(registry);
	}
	
	public RequestHandler putIfAbsent(UUID handlerId, RequestHandler handler) {
		return registry.get().putIfAbsent(handlerId, handler);
	}

	@Override
	public boolean replace(UUID id, RequestHandler oldHandler, RequestHandler newHandler) {
		return registry.get().replace(id, oldHandler, newHandler);
	}
	
	@Override
	public RequestHandler remove(Object handler) {
		return registry.get().remove(handler);
	}

	@Override
	public boolean containsKey(Object id) {
		return registry.get().containsKey(id);
	}

	@Override
	public RequestHandler get(Object id) {
		return registry.get().get(id);
	}
}
