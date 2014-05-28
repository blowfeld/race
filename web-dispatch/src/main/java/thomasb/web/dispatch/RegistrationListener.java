package thomasb.web.dispatch;

import java.lang.ref.WeakReference;

public abstract class RegistrationListener {
	private final String id;
	private final WeakReference<HandlerRegistry> registry;
	
	public RegistrationListener(String id, HandlerRegistry registry) {
		this.id = id;
		this.registry = new WeakReference<>(registry);
	}

	public abstract boolean replace(RequestHandler oldHandler);
	
	protected final boolean replaceHandler(RequestHandler oldHandler, RequestHandler newHandler) {
		return registry.get().replace(getId(), oldHandler, newHandler);
	}
	
	public RequestHandler remove() {
		return registry.get().remove(getId());
	}

	public final String getId() {
		return id;
	}
}
