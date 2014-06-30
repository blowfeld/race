package thomasb.race.web.handlers;

import thomasb.race.engine.RaceEngine;
import thomasb.race.engine.RaceTrack;
import thomasb.race.web.json.JsonConverter;
import thomasb.web.handler.Handlers;

public final class RaceContext {
	private final RaceTrack track;
	private final RaceEngine engine;
	private final JsonConverter converter;
	private final int maxTime;
	private final int updateInterval;
	private final int timeout;
	private final Handlers handlers;
	
	public RaceContext(RaceTrack track,
			RaceEngine engine,
			JsonConverter converter,
			Handlers handlers,
			int maxTime,
			int updateInterval,
			int timeout) {
		this.track = track;
		this.engine = engine;
		this.converter = converter;
		this.handlers = handlers;
		this.maxTime = maxTime;
		this.updateInterval = updateInterval;
		this.timeout = timeout;
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
	
	int getMaxTime() {
		return maxTime;
	}
	
	int getUpdateInterval() {
		return updateInterval;
	}
	
	int getTimeout() {
		return timeout;
	}
}
