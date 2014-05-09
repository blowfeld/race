var clocking = function() {
	var clock = function(interval, clockActions, dataProvider) {
		var terminate = false;
		
		var run = function() {
			terminate = false;
			tick(0);
		};
		
		var stop = function() {
			terminate = true;
		};
		
		var tick = function(count) {
			if (terminate) {
				return;
			}
			
			var start = currentTime();
			
			var process = function(data) {
				var nextTick = function() {
					clockActions.onTick(data, count, currentTime() - start);
					tick(count + 1);
				};
				setTimeout(nextTick, interval);
			};
			dataProvider.fetchData(count, process);
		};
		
		var currentTime = function() {
			return new Date().getTime();
		};
		
		return {
			run : run,
			stop : stop
		};
	};
	
	var ajaxData = function(timeout, clockActions, submissionData, url) {
		var fetchData = function(count, callback) {
			var request = ajaxRequest(count);
			request.done(callback);
			request.fail(function(jqXHR, textStatus) {
				if (textStatus === 'timeout' || jqXHR.status === 408) {
					clockActions.onTimeout();
				} else {
					window.location.href = '/ErrorPage';
				}
			});
		};
		
		var ajaxRequest = function(count) {
			return $.ajax({
				url : url,
				type : "POST",
				data : {
					time_count : count,
					data : submissionData(count)
				},
				dataType : "json",
				timeout : timeout
			});
		};
		
		return {
			fetchData : fetchData
		};
	};
	
	var serverClock = function(interval, timeout, clockActions, submissionData, url) {
		if (timeout < interval) {
			throw new Error("timeout must be larger than then interval: " + timeout + "<" + interval);
		}
		
		url = url || 'clocking';
		var dataProvider = ajaxData(timeout - interval, clockActions, submissionData);
		
		return clock(interval, clockActions, dataProvider);
	};
	
	return {
		clock : clock,
		serverClock : serverClock,
	};
}();