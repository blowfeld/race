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
			$(parent).add('<div id="#player_' + id + '"><dl><dt>id</dt><dd id="id"></dd><dt>position</dt><dd id="pos"></dd><dt>count</dt><dd id="count"></dd><dt>duration</dt><dd id="duration"></dd></dl></div>');
		};
		
		var show = function(event) {
			var displayId = parent + '.#player_' + event.id;
			$(displayId + ' #id').text(event.id);
			var posString = JSON.stringify([event.pos.x, event.pos.y]);
			$(displayId + ' #pos').text(posString);
			$(displayId + ' #count').text(event.count);
			$(displayId + ' #duration').text(event.getDuration());
		};
		
		return {
			draw : draw,
			show : show
		}
	};
	
	
	return {
		display : display,
		eventProcessor : stepProcessor
	};
}();