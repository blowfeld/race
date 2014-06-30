package thomasb.race.web.handlers;

import java.util.List;

import thomasb.race.engine.PlayerState;
import thomasb.race.engine.PlayerStatus;
import thomasb.web.clocking.ClockedRequest;
import thomasb.web.handler.RequestHandler;


final class RaceRedirect {
	private final String handlerId;
	private final int maxTime;
	
	RaceRedirect(RequestHandler handler, int maxTime) {
		this.maxTime = maxTime;
		this.handlerId = handler.getId().toString();
	}
	
	String url(List<? extends ClockedRequest<RaceData>> requests) {
		if (requests.isEmpty()) {
			return null;
		}
		
		if (requests.get(0).getTime() > maxTime) {
			return handlerId;
		}
		
		for (ClockedRequest<RaceData> request : requests) {
			PlayerState endState = request.getData().getPath().getEndState();
			if (endState.getPlayerStatus() == PlayerStatus.ACTIVE) {
				return null;
			}
		}
		
		return handlerId;
	}
}
