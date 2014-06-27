package thomasb.race.app.json;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import thomasb.race.engine.ArrowControlEvent;
import thomasb.race.engine.ControlEvent;
import thomasb.race.engine.ControlState;
import thomasb.race.engine.ControlStateImp;
import thomasb.race.engine.PathSegment;
import thomasb.race.engine.PlayerState;
import thomasb.race.engine.PlayerStatus;
import thomasb.race.engine.PointDouble;
import thomasb.race.engine.RacePlayerState;
import thomasb.race.engine.VectorPoint;

public class RaceJsonConverter implements JsonConverter {
	private static final String SEGMENT_START = "start";
	private static final String SEGMENT_END = "end";
	private static final String SEGMENT_START_TIME = "start_time";
	private static final String SEGMENT_END_TIME = "end_time";
	private static final String POINT_X = "x";
	private static final String POINT_Y = "y";
	private static final String SPEED = "speed";
	private static final String STEERING = "steering";
	private static final String POSITION = "position";
	private static final String CONTROL = "control";
	private static final String LAPS = "laps";
	private static final String STATUS = "status";

	@Override
	public JsonValue serialize(PointDouble state) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		builder.add(POINT_X, state.getX());
		builder.add(POINT_Y, state.getY());
		
		return builder.build();
	}

	@Override
	public JsonValue serialize(PlayerState state) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		builder.add(POSITION, serialize(state.getPosition()));
		builder.add(CONTROL, serialize(state.getControlState()));
		builder.add(LAPS, state.getLaps());
		builder.add(STATUS, serialize(state.getPlayerStatus()));
		
		return builder.build();
	}
	
	private JsonValue serialize(ControlState state) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		builder.add(SPEED, state.getSpeed());
		builder.add(STEERING, state.getSteering());
		
		return builder.build();
	}
	
	private JsonValue serialize(PlayerStatus status) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		
		builder.add(status.name());
		
		return builder.build().get(0);
	}

	@Override
	public JsonValue serialize(List<? extends PathSegment> path) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		
		for (PathSegment pathSegment : path) {
			builder.add(serialize(pathSegment));
		}
		
		return builder.build();
	}

	private JsonValue serialize(PathSegment segment) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		builder.add(SEGMENT_START, serialize(segment.getStart()));
		builder.add(SEGMENT_END, serialize(segment.getEnd()));
		builder.add(SEGMENT_START_TIME, segment.getStartTime());
		builder.add(SEGMENT_END_TIME, segment.getEndTime());
		
		return builder.build();
	}
	
	@Override
	public PlayerState deserializePlayerState(JsonObject json) {
		PointDouble position = deserializePointDouble(json.getJsonObject(POSITION));
		ControlState control = deserializeControlState(json.getJsonObject(CONTROL));
		int laps = json.getJsonNumber(LAPS).intValue();
		PlayerStatus status = PlayerStatus.valueOf(json.getJsonString(STATUS).getString());
		
		return new RacePlayerState(position, control, laps, status);
	}

	@Override
	public ControlEvent deserializeControlEvent(JsonNumber json) {
		return ArrowControlEvent.fromKey(json.intValue());
	}
	
	private PointDouble deserializePointDouble(JsonObject json) {
		double x = json.getJsonNumber(POINT_X).doubleValue();
		double y = json.getJsonNumber(POINT_Y).doubleValue();
		
		return new VectorPoint(x, y);
	}
	
	private ControlState deserializeControlState(JsonObject json) {
		int speed = json.getJsonNumber(SPEED).intValue();
		int steering = json.getJsonNumber(STEERING).intValue();
		
		return new ControlStateImp(speed, steering);
	}
}
