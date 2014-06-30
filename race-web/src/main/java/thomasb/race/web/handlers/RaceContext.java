package thomasb.race.web.handlers;

import thomasb.race.engine.RaceEngine;
import thomasb.race.engine.RaceTrack;
import thomasb.race.web.json.JsonConverter;

public final class RaceContext {
	private final RaceTrack track;
	private final RaceEngine engine;
	private final JsonConverter converter;
	private final int maxTime;
	private final int updateInterval;
	private final int timeout;
	
	public RaceContext(RaceTrack track,
			RaceEngine engine,
			JsonConverter converter,
			int maxTime,
			int updateInterval,
			int timeout) {
		this.track = track;
		this.engine = engine;
		this.converter = converter;
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
