package thomasb.race.app.json;

import java.util.List;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;

import thomasb.race.engine.ControlEvent;
import thomasb.race.engine.PathSegment;
import thomasb.race.engine.PlayerState;
import thomasb.race.engine.PointDouble;
import thomasb.race.engine.RaceTrack;

public interface JsonConverter {
	static final String LAP_TIME = "lapTime";
	static final String LAP_COUNT = "count";
	static final String SEGMENT_START = "start";
	static final String SEGMENT_END = "end";
	static final String SEGMENT_START_TIME = "startTime";
	static final String SEGMENT_END_TIME = "endTime";
	static final String POINT_X = "x";
	static final String POINT_Y = "y";
	static final String SPEED = "speed";
	static final String STEERING = "steering";
	static final String POSITION = "position";
	static final String CONTROL = "control";
	static final String LAPS = "laps";
	static final String STATUS = "status";
	static final String TRACK_CONTOUR = "contour";
	static final String TRACK_FINISH = "finish";
	static final String TRACK_SECTIONS = "sections";
	static final String SECTION_TYPE = "type";
	
	JsonValue serialize(String string);
	
	JsonValue serialize(PlayerState state);
	
	JsonValue serialize(List<? extends PathSegment> path);
	
	JsonValue serialize(PointDouble next);
	
	JsonValue serialize(RaceTrack track);
	

	PlayerState deserializePlayerState(JsonObject json);
	
	ControlEvent deserializeControlEvent(JsonNumber json);
}
