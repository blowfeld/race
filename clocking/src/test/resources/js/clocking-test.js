test( "clock counts and registers duration", function() {
	var testActions = function() {
		var lastCount;
		var totalDuration = 0;
		
		var onTick = function(value, count, duration) {
			lastCount = count;
			totalDuration += duration;
		};
		
		var onTimeout = function() {
			//do nothing
		};
		
		var getLastCount = function() {
			return lastCount;
		};
		
		var getAvgDuration = function() {
			return totalDuration / (lastCount + 1);
		};
		
		return {
			onTick : onTick,
			onTimeout : onTimeout,
			getLastCount : getLastCount,
			getAvgDuration : getAvgDuration
		};
	}();
	
	var dummyProvider = {
		fetchData : function(count, callback) {
			setTimeout(callback, 100);
		}
	};
	
	var clock = clocking.clock(800, testActions, dummyProvider);
	clock.run();
	
	stop();
	setTimeout(function(){
			QUnit.equal(testActions.getLastCount(), 2);
			QUnit.ok(880 < testActions.getAvgDuration() && testActions.getAvgDuration() < 920);
			clock.stop();
			start();
		}, 3000);
});
