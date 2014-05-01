var clock = function(interval) {
	var query = function(count, callback) {
		var request = $.ajax({
			url : "clocking",
			type : "POST",
			data : {
				time_count : count
			},
			dataType : "json",
		});
		
		request.done(callback);
		request.fail(function( jqXHR, textStatus) {
			alert("Request failed: " + textStatus);
		});
	}
	
	var tick = function(time, count) {
		if (time > 100 || count > 100) {
			return;
		}
		
		$('#clock').text(time);
		
		var callback = function(value) {
			var nextTick = function() {
				tick(value.time_count, count + 1);
			};
			
			setTimeout(nextTick, interval);
		};
		
		query(count, callback);
	};
	
	var run = function() {
		tick(0, 0);
	};
	
	return {
		run : run
	}
};
