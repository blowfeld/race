if(race === undefined) {
  var race = {};
}

race.controll = function() {
	var CE = clocking.events;
	
	var eventParser = function(time) {
		return function(event) {
			event.count = time;
			return CE.timedEvent(event, time, time + 1);
		};
	};
	
	
	var stepClockActions = function(raceModel, display, input, eventProcessor, redirect) {
		var lastEvent = {id:id, pos:{x:0,y:0}};
		var input = keyInput(id);
		var interval;
		var id;
		
		var init = function(initialData, interv, timeout) {
			id = initialData.id;
			
			for (var i = 0; i < initialData.participants.length; i++) {
				var participant = initialData.participants[i];
				display.add(participant);
				raceModel.addParticipant(participant, eventProcessor);
			}
			
			interval= interv;
		}
		
		var onTick = function(data, count, intervalStart, intervalEnd) {
			if (data.redirect) {
				window.location = redirect + '?' + data.redirect;
				return;
			}
			
			if (data.eventData) {
				schedule(data.eventData, count, intervalStart, intervalEnd);
				highlightTimeouts(data.eventData);
				return null;
			}
			
			onTimeout();
			console.log("server time " + data.serverTime);
			return data.serverTime;
		}
		
		var schedule = function(eventData, count, intervalStart, intervalEnd) {
			var events = eventData.map(eventParser(count));
			var rescheduleToClient = reschedule(count, intervalStart, intervalEnd);
			var rescheduledEvents = events.map(rescheduleToClient);
			raceModel.schedule(rescheduledEvents);
			
			var ownEvents = rescheduledEvents.filter(function(e) { return e.id === id; });
			lastEvent = ownEvents.sort(function(e1, e2) { return e2.getEnd() - e1.getEnd(); })[0];
			
		};
		
		var reschedule = function(count, intervalStart, intervalEnd) {
			return function(timedEvent) {
				return CE.rescheduleToClient(timedEvent, count, intervalStart, intervalEnd);
			};
		};
		
		var highlightTimeouts = function(events) {
			if (events.length === raceModel.participants()) {
				return;
			}
			
			var presentIds = events.map(function(e) { return e.id; });
			raceModel.findAbsent(presentIds).forEach(display.blink);
		}
		
		var onTimeout = function() {
			console.log("timeout");
			display.blink(id, interval);
		};
		
		var submissionData = function(count) {
			var command = input.getInput();
			var lastPosition = lastEvent.pos;
			
			return {
				id : id,
				pos : lastPosition,
				command : command
			};
		};
		
		return {
			init : init,
			onTick : onTick,
			onTimeout : onTimeout,
			submissionData : submissionData
		};
	};
	
	
	var keyInput = function() {
		var lastInput = 0;
		
		var isTracked = function(code) {
			return 36 < code && code < 41;
		};
		
		var getInput = function() {
			var result = lastInput;
			lastInput = 0;
			
			return result;
		};
		
		document.addEventListener('keydown', function(event) {
			if (!event) {
				return;
			}
			
			if(isTracked(event.keyCode)) {
				lastInput = event.keyCode;
				event.preventDefault();
				event.stopPropagation();
			} else {
				lastInput = 0;
			}
		});
		
		return {
			getInput : getInput
		};
	};
	
	
	return {
		keyInput : keyInput,
		stepClockActions : stepClockActions
	};
}();