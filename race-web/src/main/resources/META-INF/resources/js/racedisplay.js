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
		
		var draw = function(sections, finish) {
			sections = sections.reverse();
			var graphics = document.getElementById(parent);
			sections.map(drawPolygon).forEach(function(p) {
				graphics.appendChild(p);
			});
			graphics.appendChild(drawLine(finish));
		};
		
		var drawPolygon = function(section, id) {
			var path = document.createElementNS(SVG_NS, "path");
			
			path.setAttribute('d', pathDefinition(section.contour, true));
			path.setAttribute('class', 'section_type_' + section.type);
			
			return path;
		}
		
		var drawLine = function(corners, id) {
			var path = document.createElementNS(SVG_NS, "path");
			
			path.setAttribute('d', pathDefinition(corners, false));
			path.setAttribute('class', 'finish');
			
			return path;
		}
		
		var pathDefinition = function(corners, close) {
			corners = corners.map(function(c) { return (10 * c.x) + ' ' + (10 * c.y) + ' '; });
			corners.join('L');
			
			return 'M ' + corners + (close ? 'Z' : '');
		}
		
		var add = function(id, position, color) {
			var graphics = document.getElementById(parent);
			var player = document.createElementNS(SVG_NS, "circle");
			
			player.setAttribute('id', 'player_' + id);
			setCircle(player, position);
			player.setAttribute('stroke', color)
			graphics.appendChild(player)
			
			var direction = document.createElementNS(SVG_NS, "line");
			direction.setAttribute('id', 'player_state_' + id);
			setDirection(direction, position, position);
			direction.setAttribute('stroke', color)
			graphics.appendChild(direction);
		};
		
		var show = function(id, event, callback) {
			if (callback) {
				setTimeout(callback, event.getDuration());
			}
			
			var player = document.getElementById('player_' + id);
			var direction = document.getElementById('player_state_' + id);
			
			setCircle(player, event.start);
			setDirection(direction, event.start, event.end);
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
		
		var blink = function(id, duration) {
			var player = document.getElementById('player_' + id);
			var blink = document.createElementNS(SVG_NS, "animate");
			
			blink.setAttribute('from', 'visible');
			blink.setAttribute('to', 'hidden');
			blink.setAttribute('dur', 0.05);
			blink.setAttribute('repeatCount', 10);
			
			player.appendChild(blink);
			setTimeout(function() { player.removeChild(blink); },  10 * 0.05);
		}
		
		return {
			draw: draw,
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