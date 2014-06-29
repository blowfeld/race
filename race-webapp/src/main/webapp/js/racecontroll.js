if(race === undefined) {
  var race = {};
}

race.controll = function() {
	var CE = clocking.events;
	
	var eventParser = function(event) {
		return CE.timedEvent(event, event.startTime, event.endTime);
	};
	
	
	var stepClockActions = function(raceModel, display, input, eventProcessor, redirect) {
		var input = keyInput(id);
		
		var id;
		var interval;
		var currentState;
		
		var init = function(initialData, interv, timeout) {
			id = initialData.id;
			currentState = initialData.state;
			console.log(JSON.stringify(currentState));
			
			display.draw(initialData.track.sections, initialData.track.finish);
			
			for (var i = 0; i < initialData.participants.length; i++) {
				var participant = initialData.participants[i];
				var gridPosition = initialData.grid[participant];
				display.add(participant, gridPosition);
				
				raceModel.addParticipant(participant, eventProcessor(display, participant));
			}
			
			interval= interv;
		}
		
		var onTick = function(data, count, intervalStart, intervalEnd) {
			if (data.id !== id) {
				throw "Invalid request id: " + data.id;
			}
			
			if (data.redirect) {
				window.location = redirect + '?' + data.redirect;
				return;
			}
			
			if (data.serverTime) {
				onTimeout();
				console.log("server time " + data.serverTime);
				return data.serverTime;
			}
			
			//debug
			if (JSON.stringify(currentState) != JSON.stringify(data.state)) {
				console.log(JSON.stringify(data.state));
			}
			currentState = data.state;
			
			schedule(data.eventData, count, intervalStart, intervalEnd);
			highlightTimeouts(data.eventData);
			return null;
		}
		
		var schedule = function(eventData, count, intervalStart, intervalEnd) {
			for (playerId in eventData) {
				var events = eventData[playerId].map(eventParser);
				var rescheduleToClient = reschedule(count, intervalStart, intervalEnd);
				events = events.map(rescheduleToClient);
				raceModel.schedule(playerId, events);
			}
		};
		
		var reschedule = function(count, intervalStart, intervalEnd) {
			return function(timedEvent) {
				return CE.rescheduleToClient(timedEvent, count, intervalStart, intervalEnd);
			};
		};
		
		var highlightTimeouts = function(eventData) {
			var presentParticipants = Object.keys(eventData);
			if (presentParticipants.length === raceModel.participantCount()) {
				return;
			}
			
			raceModel.findAbsent(presentParticipants).forEach(display.blink);
		}
		
		var onTimeout = function() {
			console.log("timeout");
			display.blink(id, interval);
		};
		
		var submissionData = function(count) {
			var command = input.getInput();
			
			return {
				id : id,
				state : currentState,
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