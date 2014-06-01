var dispatch = function self(baseUrl, handler) {
	var run = function() {
		var url = baseUrl;
		if (handler) {
			url += "/" + handler;
		}
		
		var handler = location.search.substring(1);
		if (!handler) {
			$.ajax({
				url : url,
				success : function(response) {
					handler = response.handler;
				},
				dataType : 'json',
				async : false
			});
		}
		var handlerUrl = baseUrl + "/" + handler;
		var latch = clocking.latches.timeLatch(1000, dispatchActions, handlerUrl);
		latch.start();
	};
	
	var wait = function(remaining) {
		$('#remaining').text(remaining);
	};
	
	var redirect = function(response) {
		console.log("Red: " + baseUrl + "/dispatch.html?" + response.redirect);
		window.location = "dispatch.html?" + response.redirect;
	};
	
	var dispatchActions = {
			wait : wait,
			expire : redirect
	};
	
	return {
		run : run
	};
};