if(clocking === undefined) {
  var clocking = {};
}

clocking.events = function() {
	
	var timedEvent = function(event, start, end) {
		var that = Object.create(event);

		that.reschedule = function(newStart, newEnd) {
			return timedEvent(event, newStart, newEnd);
		};
		
		that.getStart = function() {
			return start;
		};
		
		that.getEnd = function() {
			return end;
		};
		
		that.getDuration = function() {
			return end - start;
		};
		
		return that;
	};
	
	
	var rescheduleToClient = function(serverEvent, serverStart, intervalStart, intervalEnd) {
		if (serverEvent.getStart() < serverStart || serverEvent.getEnd() > serverStart + 1) {
			throw Error("Server time out of bounds: " + serverStart + " not in [" + serverEvent.getStart() + ", " + serverEvent.getEnd() + "]");
		}
		
		var clientTime = function(serverTime) {
			var clientDuration = intervalEnd - intervalStart;
			
			return intervalStart + clientDuration * (serverTime - serverStart);
		};
		
		var newStart = clientTime(serverEvent.getStart());
		var newEnd = clientTime(serverEvent.getEnd());
		
		return serverEvent.reschedule(newStart, newEnd);
	};
	
	
	var eventQueue = function(eventProcessor) {
		var events = [];
		
		var schedule = function(newEvents) {
			var startTimeDesc = function(e1, e2) {
				return e2.getStart() - e1.getStart();
			};
			events.unshift.apply(events, newEvents.sort(startTimeDesc));
		};
		
		var execute = function() {
			if (events.length === 0) {
				setTimeout(execute, 0);
				return;
			}
			
			var event = events.pop();
			eventProcessor.process(event, execute);
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
