package thomasb.web.dispatch;

import java.lang.ref.WeakReference;
import java.util.UUID;

public abstract class RegistrationListener {
	private final UUID id;
	private final WeakReference<HandlerRegistry> registry;
	
	public RegistrationListener(UUID id, HandlerRegistry registry) {
		this.id = id;
		this.registry = new WeakReference<>(registry);
	}
	
	public RequestHandler putIfAbsent(UUID handlerId, RequestHandler handler) {
		return registry.get().putIfAbsent(handlerId, handler);
	}

	public abstract boolean replace(RequestHandler oldHandler);
	
	protected final boolean replaceHandler(RequestHandler oldHandler, RequestHandler newHandler) {
		return registry.get().replace(getId(), oldHandler, newHandler);
	}
	
	public RequestHandler remove() {
		return registry.get().remove(getId());
	}

	public final UUID getId() {
		return id;
	}
}
