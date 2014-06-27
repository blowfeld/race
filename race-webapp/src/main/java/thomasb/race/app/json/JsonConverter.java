package thomasb.race.app.json;

import java.util.List;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;

import thomasb.race.engine.ControlEvent;
import thomasb.race.engine.PathSegment;
import thomasb.race.engine.PlayerState;
import thomasb.race.engine.PointDouble;

public interface JsonConverter {
	static final String LAP_TIME = "lapTime";
	static final String LAP_COUNT = "count";
	static final String SEGMENT_START = "start";
	static final String SEGMENT_END = "end";
	static final String SEGMENT_START_TIME = "start_time";
	static final String SEGMENT_END_TIME = "end_time";
	static final String POINT_X = "x";
	static final String POINT_Y = "y";
	static final String SPEED = "speed";
	static final String STEERING = "steering";
	static final String POSITION = "position";
	static final String CONTROL = "control";
	static final String LAPS = "laps";
	static final String STATUS = "status";
	
	public JsonValue serialize(PlayerState state);
	
	public JsonValue serialize(List<? extends PathSegment> path);
	
	public JsonValue serialize(PointDouble next);
	
	
	public PlayerState deserializePlayerState(JsonObject json);
	
	public ControlEvent deserializeControlEvent(JsonNumber json);
}
