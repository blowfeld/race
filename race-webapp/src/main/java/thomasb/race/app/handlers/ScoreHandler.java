package thomasb.race.app.handlers;

import static thomasb.race.app.handlers.RaceProcessor.COLOR_PARAMETER;
import static thomasb.race.app.handlers.RaceProcessor.ID_PARAMETER;
import static thomasb.race.app.handlers.RaceProcessor.SCORE_PARAMETER;

import java.io.IOException;
import java.io.StringReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.servlet.ServletException;

import thomasb.race.app.json.JsonConverter;
import thomasb.race.engine.Lap;
import thomasb.web.handler.HandlerContext;
import thomasb.web.handler.RequestHandler;

public class ScoreHandler extends CountDownHandler {
	private static final Comparator<Entry<String, Lap>> SCORE_COMPARATOR = new Comparator<Map.Entry<String,Lap>>() {
		@Override
		public int compare(Entry<String, Lap> o1, Entry<String, Lap> o2) {
			return o1.getValue().compareTo(o2.getValue());
		}
	};
	
	private static final String NAME_PARAMETER = "name";
	private static final String LAPS_PARAMETER = "laps";
	
	private final Map<String, String> participantNames;
	private final Map<String, Lap> participantScores = new HashMap<>();
	
	private final JsonConverter converter;
	
	private ExpirationListener listener;
	private volatile boolean launched = false;
	
	public ScoreHandler(List<String> participants, Map<String, String> participantNames, JsonConverter converter) {
		super(participants, 60000, 500);
		this.participantNames = participantNames;
		this.converter = converter;
	}
	
	@Override
	public void handle(HandlerContext context) throws ServletException, IOException {
		if (!launched) {
			launched = true;
			launch();
		}
		
		String participant = context.getRequest().getSession().getId();
		synchronized (participantScores) {
			if (!participantScores.containsKey(participant)) {
				String rawScore = context.getRequest().getParameter(SCORE_PARAMETER);
				JsonObject jsonScore = Json.createReader(new StringReader(rawScore)).readObject();
				participantScores.put(participant, converter.deserializeLaps(jsonScore));
			}
		}
		
		context.setResponseParameter(SCORE_PARAMETER, createRanking());
		
		super.handle(context);
	}
	
	private JsonValue createRanking() {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		
		Set<Entry<String, Lap>> sortedParticipants = new TreeSet<>(SCORE_COMPARATOR);
		sortedParticipants.addAll(participantScores.entrySet());
		
		for (Entry<String, Lap> participantEntry : sortedParticipants) {
			int index = getParticipants().indexOf(participantEntry.getKey());
			JsonObjectBuilder scoreBuilder = Json.createObjectBuilder();
			scoreBuilder.add(ID_PARAMETER, participantEntry.getKey())
					.add(COLOR_PARAMETER, PlayerColors.INSTANCE.get(index))
					.add(NAME_PARAMETER, participantNames.get(participantEntry.getKey()))
					.add(LAPS_PARAMETER, converter.serialize(participantEntry.getValue()));
			builder.add(scoreBuilder);
		}
		
		return builder.build();
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
