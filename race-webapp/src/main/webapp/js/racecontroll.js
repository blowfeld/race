if(race === undefined) {
  var race = {};
}

race.controll = function() {
	
	var eventParser = function(time) {
		return function(event) {
			event.count = time;
			return CE.timedEvent(event, time, time + 1);
		};
	};
	
	
	var stepClockActions = function(raceModel, input, eventProcessor, redirect) {
		var lastEvent = {id:id, pos:{x:0,y:0}};
		var input = keyInput(id);
		var interval;
		var id;
		
		var init(initalData, interv, timeout) {
			id = initialData.id;
			
			for (var i = 0; i < initialData.participants.length; i++) {
				var id = initialData.participants[i];
				raceModel.addParticipant(id, eventProcessor);
			}
			
			interval= interv;
		}
		
		var onTick = function(data, count, intervalStart, intervalEnd) {
			if (data.redirect) {
				window.location = redirect + data.redirect;
				return;
			}
			
			schedule(data.eventData);
		}
		
		var schedule = function(eventData) {
			var events = eventData.map(eventParser(count));
			var rescheduleToClient = reschedule(count, intervalStart, intervalEnd);
			var rescheduledEvents = events.map(rescheduleToClient);
			raceModel.schedule(rescheduledEvents);
		};
		
		var reschedule = function(count, intervalStart, intervalEnd) {
			return function(timedEvent) {
				return CE.rescheduleToClient(timedEvent, count, intervalStart, intervalEnd);
			};
		};
		
		var onTimeout = function() {
			console.log("timeout");
			//do nothing
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
			} else {
				lastInput = 0;
			}
			event.preventDefault();
			event.stopProgation();
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