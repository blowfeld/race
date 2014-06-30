package thomasb.race.web.handlers;

import java.nio.file.Path;

import thomasb.race.engine.RaceEngine;
import thomasb.race.engine.RaceTrack;
import thomasb.race.web.json.JsonConverter;
import thomasb.web.handler.Handlers;

public final class RaceContext {
	private final RaceTrack track;
	private final RaceEngine engine;
	private final JsonConverter converter;
	private final Handlers handlers;
	private final int maxTime;
	private final int updateInterval;
	private final int timeout;
	private final int registrationInterval;
	private final int launchInterval;
	private final int scoreInterval;
	private final int countdownResolution;
	private final Path scoresFile;
	
	public RaceContext(RaceTrack track,
			RaceEngine engine,
			JsonConverter converter,
			Handlers handlers,
			Path scoresFile,
			int maxTime,
			int updateInterval,
			int timeout,
			int registrationInterval,
			int launchInterval,
			int scoreInterval,
			int countdownResolution) {
		this.track = track;
		this.engine = engine;
		this.converter = converter;
		this.handlers = handlers;
		this.scoresFile = scoresFile;
		this.maxTime = maxTime;
		this.updateInterval = updateInterval;
		this.timeout = timeout;
		this.registrationInterval = registrationInterval;
		this.launchInterval = launchInterval;
		this.scoreInterval = scoreInterval;
		this.countdownResolution = countdownResolution;
	}
	
	RaceTrack getTrack() {
		return track;
	}
	
	RaceEngine getEngine() {
		return engine;
	}
	
	JsonConverter getConverter() {
		return converter;
	}
	
	Handlers getHandlers() {
		return handlers;
	}
	
	int getMaxCount() {
		return maxTime;
	}
	
	int getMaxCountAfterWinner() {
		return maxTime / 3;
	}
	
	int getRegistrationInterval() {
		return registrationInterval;
	}
	
	int getLaunchInterval() {
		return launchInterval;
	}
	
	int getScoreInterval() {
		return scoreInterval;
	}
	
	int getCountdownResolution() {
		return countdownResolution;
	}
	
	int getRegistration() {
		return maxTime / 3;
	}
	
	int getUpdateInterval() {
		return updateInterval;
	}
	
	int getTimeout() {
		return timeout;
	}
	
	public Path getScoresFile() {
		return scoresFile;
	}
}
