package thomasb.race.web.handlers;

import static com.google.common.base.Charsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static thomasb.race.web.handlers.RaceProcessor.COLOR_PARAMETER;
import static thomasb.race.web.handlers.RaceProcessor.ID_PARAMETER;
import static thomasb.race.web.handlers.RaceProcessor.SCORE_PARAMETER;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
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

import thomasb.race.engine.Lap;
import thomasb.race.web.json.JsonConverter;
import thomasb.web.handler.HandlerContext;
import thomasb.web.handler.Handlers;
import thomasb.web.handler.RequestHandler;

import com.google.common.collect.ImmutableList;

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
	private final Path scoresFile;
	
	private ExpirationListener listener;
	private boolean scoresWritten;
	private volatile boolean launched = false;
	
	public ScoreHandler(List<String> participants,
			Map<String, String> participantNames,
			JsonConverter converter,
			Handlers handlers,
			Path scoresFile) {
		super(participants, 20000, 500, handlers);
		this.participantNames = participantNames;
		this.converter = converter;
		this.scoresFile = scoresFile;
	}
	
	@Override
	public void handle(HandlerContext context) throws ServletException, IOException {
		if (!launched) {
			launched = true;
			launch();
		}
		
		handleScores(context);
		
		super.handle(context);
	}

	private synchronized void handleScores(HandlerContext context) {
		String participant = context.getRequest().getSession().getId();
		if (!participantScores.containsKey(participant)) {
			String rawScore = context.getRequest().getParameter(SCORE_PARAMETER);
			JsonObject jsonScore = Json.createReader(new StringReader(rawScore)).readObject();
			participantScores.put(participant, converter.deserializeLaps(jsonScore));
		}
		
		JsonValue scores = createRanking();
		context.setResponseParameter(SCORE_PARAMETER, scores);
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
		writeScores();
		if (allParticipantsClosed()) {
			listener.expire();
		}
	}
	
	private synchronized void writeScores() {
		if (scoresWritten) {
			return;
		}
		scoresWritten = true;
		
		String scoreString = createRanking().toString();
		try {
			Files.write(scoresFile, ImmutableList.of(scoreString), UTF_8, APPEND);
		} catch (IOException e) {
			new RuntimeException("Error writing score file: " + scoresFile.toString(), e);
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
