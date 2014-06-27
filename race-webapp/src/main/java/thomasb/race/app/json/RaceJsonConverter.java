package thomasb.race.app.json;

import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import thomasb.race.engine.ControlEvent;
import thomasb.race.engine.ControlState;
import thomasb.race.engine.PathSegment;
import thomasb.race.engine.PlayerState;
import thomasb.race.engine.PointDouble;

public class RaceJsonConverter implements JsonConverter {
	private static final String POINT_X = "x";
	private static final String POINT_Y = "y";

	@Override
	public JsonStructure serialize(PointDouble state) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		builder.add(POINT_X, state.getX());
		builder.add(POINT_Y, state.getY());
		
		return builder.build();
	}
	
	public static PointDouble deserialize(String json) {
		return null;
	}
	
	public static JsonStructure serialize(ControlState state) {
		return null;
	}

	@Override
	public JsonStructure serialize(PlayerState state) {
		return null;
	}

	@Override
	public JsonStructure serialize(List<? extends PathSegment> path) {
		return null;
	}

	@Override
	public PlayerState deserializePlayerState(JsonObject json) {
		return null;
	}

	@Override
	public ControlEvent deserializeControlEvent(JsonValue json) {
		return null;
	}
}
