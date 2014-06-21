if(race === undefined) {
  var race = {};
}

race.display = function() {
	
	var stepProcessor = function(display) {
		var process = function(event) {
			display.show(event);
		};
		
		return {
			process : process
		};
	};
	
	
	var display = function(parent) {
		var add = function(id) {
			$(parent).append('<div id="player_' + id + '"><dl><dt>id</dt><dd class="id"></dd><dt>position</dt><dd class="pos"></dd><dt>count</dt><dd class="count"></dd><dt>duration</dt><dd class="duration"></dd></dl></div>');
		};
		
		var show = function(event) {
			var displayId = '#player_' + event.id;
			$(displayId + ' .id').text(event.id);
			var posString = JSON.stringify([event.pos.x, event.pos.y]);
			$(displayId + ' .pos').text(posString);
			$(displayId + ' .count').text(event.count);
			$(displayId + ' .duration').text(event.getDuration());
			console.log('show ' + displayId);
		};
		
		var blink = function(id, duration) {
			var displayId = '#player_' + id;
			$(displayId).effect("pulsate", {}, duration);
		}
		
		return {
			add : add,
			show : show,
			blink : blink
		}
	};
	
	
	return {
		display : display,
		eventProcessor : stepProcessor
	};
}();