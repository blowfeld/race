package thomasb.race.app.handlers;

import java.io.IOException;
import java.util.Map;

import javax.json.JsonValue;
import javax.servlet.ServletException;

import thomasb.web.handler.HandlerContext;
import thomasb.web.handler.RequestHandler;

import com.google.common.collect.ImmutableList;

public class ScoreHandler extends CountDownHandler {
	private static final String RANKING = "ranking";
	
	private final Map<String, String> participantNames;
	
	private ExpirationListener listener;
	private volatile boolean launched = false;
	
	public ScoreHandler(Map<String, String> participantNames) {
		super(ImmutableList.copyOf(participantNames.keySet()), 2000, 500);
		this.participantNames = participantNames;
	}
	
	@Override
	public void handle(HandlerContext context) throws ServletException, IOException {
		if (!launched) {
			launched = true;
			launch();
		}
		
		System.err.println(participantNames);
		context.setResponseParameter(RANKING, createRanking());
		
		super.handle(context);
	}
	
	private JsonValue createRanking() {
		return JsonValue.FALSE;
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
