if(clocking === undefined) {
  var clocking = {};
}

clocking.clocks = function() {
	var clock = function(clockActions, dataProvider) {
		var terminate = false;
		var interval;
		var timeout = 0; // infinite ? (undocumented..)
		
		var run = function() {
			terminate = false;
			init();
			tick(0);
		};
		
		var stop = function() {
			terminate = true;
		};
		
		var init = function() {
			var request = ajaxRequest(-1, false);
			request.done(function(response) {
				interval = response.interval;
				timeoutInterval = response.timeout;
				clockActions = clockActions.withInitalData(response.data);
			});
			
			if (timeout < interval) {
				throw new Error("timeout must be larger than then interval: " + timeout + "<" + interval);
			}
		};
		
		var tick = function(count) {
			if (terminate) {
				return;
			}
			
			var start = currentTime();
			
			var process = function(response) {
				var nextTick = function() {
					clockActions.onTick(response.data, count, start, currentTime());
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
	
	
	var ajaxRequest = function(count, async) {
		return $.ajax({
			url : url,
			type : "POST",
			data : {
				time_count : count,
				data : JSON.stringify(clockActions.submissionData(count))
			},
			dataType : "json",
			timeout : timeout,
			async : typeof async !== 'undefined' ? async : true
		});
	};
	
	
	var ajaxData = function(clockActions, url) {
		var fetchData = function(count, callback) {
			var request = ajaxRequest(count);
			request.done(callback);
			request.fail(function(jqXHR, textStatus) {
				if (textStatus === 'timeout' || jqXHR.status === 408) {
					clockActions.onTimeout();
				} else {
//					window.location.href = '/ErrorPage';
				}
			});
		};
		
		return {
			fetchData : fetchData
		};
	};
	
	
	var serverClock = function(clockActions, url) {
		url = url || 'clocking';
		var dataProvider = ajaxData(clockActions, url);
		
		return clock(clockActions, dataProvider);
	};
	
	
	return {
		clock : clock,
		serverClock : serverClock,
	};
}();