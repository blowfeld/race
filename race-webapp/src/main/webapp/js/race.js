$(document).ready(function() {
	var CC = clocking.clocks;
	var RM = race.model;
	var RD = race.display;
	var RC = race.controll;

	var model = RM.raceModel();
	var disp = RD.display('#race');
	
	var clockActions = RC.stepClockActions(model,
			disp,
			RC.keyInput(),
			RD.eventViewer,
			'scores.html');
	
	var clock = CC.serverClock(clockActions, 'core/' + location.search.substring(1));
	
	clock.run();
});
