if(clocking === undefined) {
  var clocking = {};
}

clocking.events = function() {

	
	var timedEvent = function(event, start, end) {
		var reschedule = function(newStart, newEnd) {
			return timedEvent(event, newStart, newEnd);
		};
		
		var getStart = function() {
			return start;
		};
		
		var getEnd = function() {
			return end;
		};
		
		var getDuration = function() {
			return end - start;
		};
		
		return {
			prototype : event,
			reschedule : reschedule,
			shift : shift
			getStart : getStart,
			getEnd : getEnd,
			getDuration : getDuration,
		};
	};
	
	
	var rescheduleToClient = function(serverEvent, startCount, clientStart, clientDuration) {
		if (serverEvent.getStart() < startCount || serverEvent.getEnd() >= startCount + 1) {
			throw Error("Server time out of bounds: " + serverEvent.getTime());
		}
		
		var clientTime = function(serverTime) {
			return clientStart + clientDuration * (serverTime - startCount);
		};
		
		var newStart = clientTime(timedEvent.getStart());
		var newEnd = clientTime(timedEvent.getEnd());
		
		return timedEvent.reschedule(newStart, newEnd);
	};
	
	
	var eventQueue = function(eventProcessor) {
		events = [];
		
		var schedule = function(newEvents) {
			events = newEvents.concat(events);
		};
		
		var execute = function() {
			if (!events) {
				setTimeout(execute, 0);
			}
			
			event = events.pop();
			eventProcessor(event, execute);
		};
		
		return {
			schedule : schedule,
			execute : execute
		};
	};
	
	
	return {
		timedEvent : timedEvent,
		rescheduleToClient : rescheduleToClient,
		eventQueue: eventQueue
	};
}();
