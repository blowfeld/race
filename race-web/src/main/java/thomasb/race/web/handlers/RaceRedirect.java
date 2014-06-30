package thomasb.race.web.handlers;

import java.util.List;

import thomasb.race.engine.PlayerState;
import thomasb.race.engine.PlayerStatus;
import thomasb.web.clocking.ClockedRequest;
import thomasb.web.handler.RequestHandler;


final class RaceRedirect {
	private final String handlerId;
	private final int maxCountAfterWinner;
	private int maxCount;
	private volatile boolean hasWinner = false;
	
	RaceRedirect(RequestHandler handler,
			int maxCount,
			int maxCountAfterWinner) {
		this.maxCount = maxCount;
		this.maxCountAfterWinner = maxCountAfterWinner;
		this.handlerId = handler.getId().toString();
	}
	
	String url(List<? extends ClockedRequest<RaceData>> requests) {
		if (requests.isEmpty()) {
			return null;
		}
		
		if (requests.get(0).getTime() > maxCount) {
			return handlerId;
		}
		
		for (ClockedRequest<RaceData> request : requests) {
			PlayerState endState = request.getData().getPath().getEndState();
			if (!hasWinner && endState.getPlayerStatus() == PlayerStatus.FINISHED) {
				hasWinner = true;
				maxCount = request.getTime() + maxCountAfterWinner;
			}
			
			if (endState.getPlayerStatus() == PlayerStatus.ACTIVE) {
				return null;
			}
		}
		
		return handlerId;
	}
}
