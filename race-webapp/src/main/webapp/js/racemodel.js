if(race === undefined) {
  var race = {};
}

race.model = function() {
	var CE = clocking.events;
	
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
	
	
	var raceModel = function() {
		var participants = {};
		
		var addParticipant = function(id, eventProcessor) {
			var eventQueue = CE.eventQueue(eventProcessor);
			participants[id] = add(id, eventQueue);
		};
		
		var add = function(id, eventQueue) {
			var player = participant(id, eventQueue);
			participants[id] = player;
			player.start();
		};
		
		var schedule = function(eventData) {
			for (var i = 0; i < events.length; i++) {
				var event = events[i];
				participants[event.id].schedule(event);
			}
		};
		
		return {
			addParticipant : addParticipant,
			add : add,
			schedule : schedule
		};
	}
	
	
	return {
		raceModel : raceModel
	};
}();