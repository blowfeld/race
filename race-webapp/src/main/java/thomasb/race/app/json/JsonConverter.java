package thomasb.race.app.json;

import java.util.List;

import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import thomasb.race.engine.ControlEvent;
import thomasb.race.engine.PathSegment;
import thomasb.race.engine.PlayerState;
import thomasb.race.engine.PointDouble;

public interface JsonConverter {
	
	public JsonStructure serialize(PlayerState state);
	
	public JsonStructure serialize(List<? extends PathSegment> path);
	
	public JsonStructure serialize(PointDouble next);
	
	
	public PlayerState deserializePlayerState(JsonObject json);
	
	public ControlEvent deserializeControlEvent(JsonValue json);
}
