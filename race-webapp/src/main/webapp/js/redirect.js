var redirectLatch = function self(dispatchUrl,redirectUrl, waitAction, init) {
	var run = function() {
		var handler = location.search.substring(1);
		if (init || !handler) {
			$.ajax({
				url : dispatchUrl,
				type : 'POST',
				success : function(response) {
					handler = response.handler;
					init.callback(response);
				},
				data : init.data || '',
				dataType : 'json',
				async : false
			});
		}
		var handlerUrl = dispatchUrl + "/" + handler;
		var latch = clocking.latches.timeLatch(1000, redirectActions, handlerUrl);
		latch.start();
	};
	
	
	var redirectActions = function() {
		var wait = function(remaining) {
			waitAction(remaining);
		};
		
		var redirect = function(response) {
			window.location = redirectUrl + "?" + response.redirect;
		};
		
		return {
			wait : wait,
			expire : redirect
		}
	}();
	
	return {
		run : run
	};
};