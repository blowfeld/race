if(race === undefined) {
  var race = {};
}

race.display = function() {
	
	var eventViewer = function(display, id) {
		var process = function(event) {
			display.show(id, event);
		};
		
		return {
			process : process
		};
	};
	
	
	var display = function(parent) {
		var add = function(id, position) {
			$(parent).append('<div id="player_' + id + '"><dl><dt>id</dt><dd class="id"></dd><dt>position</dt><dd class="pos"> ' + JSON.stringify([position.x, position.y]) + ' </dd><dt>count</dt><dd class="count"></dd><dt>duration</dt><dd class="duration"></dd></dl></div>');
		};
		
		var show = function(id, event) {
			var displayId = '#player_' + id;
			$(displayId + ' .id').text(id);
			var posString = JSON.stringify([event.start.x, event.end.y]);
			$(displayId + ' .pos').text(posString);
			$(displayId + ' .count').text(event.getStart());
			$(displayId + ' .duration').text(event.getDuration());
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
		eventViewer : eventViewer
	};
}();