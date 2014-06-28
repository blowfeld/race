package thomasb.race.app.handlers;

import javax.json.JsonString;

import thomasb.race.engine.RacePath;

final class RaceData {
	private final JsonString id;
	private final RacePath path;

	RaceData(JsonString id, RacePath path) {
		this.id = id;
		this.path = path;
	}
	
	JsonString getJsonId() {
		return id;
	}
	
	String getId() {
		return id.getString();
	}
	
	RacePath getPath() {
		return path;
	}

}
