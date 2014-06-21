if(clocking === undefined) {
  var clocking = {};
}

clocking.clocks = function() {
	var clock = function(clockActions, dao) {
		var terminate = false;
		var interval;
		var timeout = 0; // infinite ? (undocumented..)
		
		var run = function() {
			terminate = false;
			
			var initActions = function(initial) {
				interval = initial.interval;
				timeout = initial.timeout;
				clockActions.init(initial.data);
				
				if (timeout >= interval) {
					throw new Error("timeout was larger than interval: " + timeout + " > " + interval);
				}
				
				setTimeout(function() { tick(0); }, interval);
			}
			
			dao.fetchInitial(initActions);
		};
		
		var stop = function() {
			terminate = true;
		};
		
		var tick = function(count) {
			if (terminate) {
				return;
			}
			
			var start = currentTime();
			
			var processResponse = function(response) {
				var serverTime = clockActions.onTick(response.data, count, start, currentTime());
				
				var nextTick = function() {
					tick(serverTime || count + 1);
				};
				setTimeout(nextTick, interval);
			};
			
			var onTimeout = function() {
				clockActions.onTimeout();
				tick(count + 1);
			};
			
			dao.fetchData(count, processResponse, onTimeout, timeout);
		};
		
		var currentTime = function() {
			return new Date().getTime();
		};
		
		return {
			run : run,
			stop : stop
		};
	};
	
	
	var ajaxDao = function(clockActions, url, errorUrl) {
		var ajaxRequest = function(count, data, async, timeout) {
			return $.ajax({
				url : url,
				type : "POST",
				data : {
					time_count : count,
					data : data
				},
				dataType : "json",
				timeout : timeout,
				async : true
			});
		};
		
		var fetchInitial = function(callback) {
			var request = ajaxRequest(-1, {}, 0);
			request.done(callback);
		};
		
		var fetchData = function(count, onSuccess, onTimeout, timeout) {
			var data = JSON.stringify(clockActions.submissionData(count));
			var request = ajaxRequest(count, data, timeout);
			request.done(onSuccess);
			request.fail(function(jqXHR, textStatus) {
				if (textStatus === 'timeout' || jqXHR.status === 408) {
					onTimeout();
				} else {
					window.location.href = errorUrl;
				}
			});
		};
		
		return {
			fetchInitial : fetchInitial,
			fetchData : fetchData
		};
	};
	
	
	var serverClock = function(clockActions, url, errorUrl) {
		return clock(clockActions, ajaxDao(clockActions, url, errorUrl));
	};
	
	
	return {
		clock : clock,
		serverClock : serverClock,
	};
}();