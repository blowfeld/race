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
import thomasb.race.engine.Lap;
import thomasb.race.engine.PathSegment;
import thomasb.race.engine.PlayerState;
import thomasb.race.engine.PlayerStatus;
import thomasb.race.engine.PointDouble;
import thomasb.race.engine.RaceLap;
import thomasb.race.engine.RacePlayerState;
import thomasb.race.engine.RaceTrack;
import thomasb.race.engine.TrackSection;
import thomasb.race.engine.VectorPoint;
import thomasb.race.web.json.JsonConverter;

public class RaceJsonConverter implements JsonConverter {
	
	@Override
	public JsonValue serialize(String string) {
		return Json.createArrayBuilder().add(string).build().getJsonString(0);
	}
	
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
		builder.add(LAPS, serialize(state.getLaps()));
		builder.add(STATUS, serialize(state.getPlayerStatus()));
		
		return builder.build();
	}
	
	private JsonValue serialize(ControlState state) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		builder.add(SPEED, state.getSpeed());
		builder.add(STEERING, state.getSteering());
		
		return builder.build();
	}
	
	@Override
	public JsonValue serialize(Lap laps) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		builder.add(LAP_COUNT, laps.getCount());
		builder.add(LAP_TIME, laps.getLapTime());
		
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
	public JsonValue serialize(RaceTrack track) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		builder.add(TRACK_FINISH, serializePath(track.getFinish()));
		builder.add(TRACK_SECTIONS, serializeSections(track.getSections()));
		
		return builder.build();
	}
	
	private JsonValue serializePath(List<? extends PointDouble> path) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		
		for (PointDouble corner : path) {
			builder.add(serialize(corner));
		}
		
		return builder.build();
	}
	
	private JsonValue serializeSections(List<? extends TrackSection> sections) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		
		for (TrackSection section : sections) {
			builder.add(serialize(section));
		}
		
		return builder.build();
	}
	
	private JsonValue serialize(TrackSection section) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		builder.add(SECTION_TYPE, section.getType().getMaxSpeed());
		builder.add(TRACK_CONTOUR, serializePath(section.getCorners()));
		
		return builder.build();
	}
	
	@Override
	public PlayerState deserializePlayerState(JsonObject json) {
		PointDouble position = deserializePointDouble(json.getJsonObject(POSITION));
		ControlState control = deserializeControlState(json.getJsonObject(CONTROL));
		Lap laps = deserializeLaps(json.getJsonObject(LAPS));
		PlayerStatus status = PlayerStatus.valueOf(json.getJsonString(STATUS).getString());
		
		return new RacePlayerState(position, control, laps, status);
	}
	
	@Override
	public Lap deserializeLaps(JsonObject json) {
		int count = json.getJsonNumber(LAP_COUNT).intValue();
		double lapTime = json.getJsonNumber(LAP_TIME).doubleValue();
		
		return new RaceLap(count, lapTime);
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
