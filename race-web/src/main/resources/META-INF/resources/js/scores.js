var displayScores = function(parent, scores) {
	var createEntry = function(score) {
		var entry = $('<tr>');
		entry.append($('<td>')
				.append(colorDiv(score.colors)));
		entry.append($('<td>')
				.text(score.name));
		entry.append($('<td>')
				.text(score.laps.count));
		entry.append($('<td>')
				.text(score.laps.lapTime));
		
		return entry;
	}
	
	var colorDiv = function(color) {
		var div = $('<div>');
		
		div.attr('class', 'color_rect');
		div.css('background-color', color);
		
		return div;
	}
	
	var parentNode = $("#" + parent).find('tbody');
	parentNode.empty();
	
	parentNode.append(scores.map(createEntry));
}

