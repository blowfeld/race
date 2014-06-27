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
	
	public JsonValue serialize(PlayerState state);
	
	public JsonValue serialize(List<? extends PathSegment> path);
	
	public JsonValue serialize(PointDouble next);
	
	
	public PlayerState deserializePlayerState(JsonObject json);
	
	public ControlEvent deserializeControlEvent(JsonNumber json);
}
