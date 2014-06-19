package thomasb.race.app.handlers;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import thomasb.web.handler.HandlerContext;
import thomasb.web.handler.RequestHandler;

public class ScoreHandler extends CountDownHandler {
	private ExpirationListener listener;
	
	private volatile boolean launched = false;
	
	public ScoreHandler(List<String> participants) {
		super(participants, 2000, 500);
	}
	
	@Override
	public void handle(HandlerContext context) throws ServletException, IOException {
		if (!launched) {
			launched = true;
			launch();
		}
		
		super.handle(context);
	}

	
	@Override
	protected void onExpire() {
		if (getParticipants().isEmpty()) {
			listener.expire();
		}
	}
	
	public void setExpirationListener(ExpirationListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected RequestHandler getSuccessor() {
		return null;
	}
	
	public static interface ExpirationListener {
		void expire();
	}
}
