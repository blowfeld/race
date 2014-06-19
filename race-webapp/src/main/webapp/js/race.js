$(document).ready(function() {
	var CC = clocking.clocks;
	var RM = race.model;
	var RD = race.display;
	var RC = race.conroll;

	var model = RM.raceModel();
	var disp = RD.display('#race');
	
	var clockActions = RC.stepClockActions(model,
			RC.keyInput(),
			RD.eventProcessor(disp),
			'scores.html');
	
	var clock = CC.serverClock(700,
			10000,
			clockActions,
			'core/' + location.search.substring(1));
	
	clock.run();
});
