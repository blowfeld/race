if(race === undefined) {
  var race = {};
}

race.display = function() {
	
	var eventViewer = function(display, id) {
		var process = function(event, callback) {
			display.show(id, event, callback);
		};
		
		return {
			process : process
		};
	};
	
	
	var display = function(parent) {
		var SVG_NS = "http://www.w3.org/2000/svg";
		
		var RADIUS = 10;
		
		var add = function(id, position) {
			var graphics = document.getElementById(parent);
			var player = document.createElementNS(SVG_NS, "circle");
			
			player.setAttribute('id', 'player_' + id);
			setCircle(player, position);
			graphics.appendChild(player)
			
			var direction = document.createElementNS(SVG_NS, "line");
			direction.setAttribute('id', 'player_state_' + id);
			setDirection(direction, position, position);
			graphics.appendChild(direction);
			
			var label = document.createElementNS(SVG_NS, "text");
			label.setAttribute('id', 'player_label_' + id);
			setLabel(label, position, position, 0, 0);
			graphics.appendChild(label);
		};
		
		var show = function(id, event, callback) {
			if (callback) {
				setTimeout(callback, event.getDuration());
			}
			
			var player = document.getElementById('player_' + id);
			var direction = document.getElementById('player_state_' + id);
			var label = document.getElementById('player_label_' + id);
			
			setCircle(player, event.start);
			setDirection(direction, event.start, event.end);
			setLabel(label, event.start, event.end, event.startTime, event.getDuration());
		};
		
		var setCircle = function(circle, position) {
			circle.setAttribute('cx', position.x * 10);
			circle.setAttribute('cy', position.y * 10);
			circle.setAttribute('r', RADIUS);
		}
		
		var setDirection = function(direction, start, end) {
			direction.setAttribute('x1', start.x * 10);
			direction.setAttribute('y1', start.y * 10);
			direction.setAttribute('x2', end.x * 10);
			direction.setAttribute('y2', end.y * 10);
		}
		
		var setLabel = function(label, start, end, time, duration) {
			label.setAttribute("x", start.x * 10 + RADIUS);
			label.setAttribute("y", start.y * 10 - RADIUS);
			label.textContent = JSON.stringify([start, end, time, duration]);
		}
		
		var blink = function(id, duration) {
//			$('#player_' + id).effect("pulsate", {}, duration);
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