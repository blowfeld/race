(function() {
	var CE = clocking.events;
	var CC = clocking.clocks;

	
	var eventParser = function(time) {
		return function(event) {
			event.count = time;
			return CE.timedEvent(event, time, time + 1);
		};
	};
	
	
	var stepClockActions = function(id, participants, interval) {
		var lastEvent = {id:id, pos:{x:0,y:0}};
		var input = keyInput(id);
		
		var onTick = function(data, count, intervalStart, intervalEnd) {
			var events = data.map(eventParser(count));
			var rescheduleToClient = reschedule(count, intervalStart, intervalEnd);
			var rescheduledEvents = events.map(rescheduleToClient);
			queue(rescheduledEvents);
		};
		
		var reschedule = function(count, intervalStart, intervalEnd) {
			return function(timedEvent) {
				return CE.rescheduleToClient(timedEvent, count, intervalStart, intervalEnd);
			};
		};
		
		var queue = function(events) {
			for (var i = 0; i < events.length; i++) {
				var event = events[i];
				if (event.id === id) {
					participants.self.schedule(event);
					lastEvent = event;
				} else {
					participants.other.schedule(event);
				}
			}
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
	
	
	var participant = function(id, eventQueue) {
		var schedule = function(event) {
			eventQueue.schedule(event);
		};
		
		var getId = function() {
			return id;
		};
		
		var start = function() {
			eventQueue.execute();
		};
		
		return {
			schedule : schedule,
			getId : getId,
			start : start
		};
	};

	
	var stepProcessor = function(displayId) {
		var process = function(event, next) {
			console.log("processed ");
			display(event);
			next();
		};
		
		var display = function(event) {
			$(displayId + ' #id').text(event.id);
			var posString = JSON.stringify([event.pos.x, event.pos.y]);
			$(displayId + ' #pos').text(posString);
			$(displayId + ' #count').text(event.count);
			$(displayId + ' #duration').text(event.getDuration());
		}
		
		return {
			process : process
		};
	}
	
	var keyInput = function(id) {
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

	
	var uuid = function () {
		return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
	};
	
	
	$(document).ready(function() {
		var id = uuid();
		var selfQueue = CE.eventQueue(stepProcessor('#self'));
		var otherQueue = CE.eventQueue(stepProcessor('#other'));
		var participants = {
				self : participant(id, selfQueue),
				other : participant(undefined, otherQueue)
		};
		
		var clockActions = stepClockActions(id, participants, 700);
		
		var clock = CC.serverClock(700, 100000, clockActions, 'steps');
		participants.self.start();
		participants.other.start();
		clock.run();
	});
})();
