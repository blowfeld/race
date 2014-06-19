var pos_1;
var pos_2;

module("Position tests", {
	setup: function () {
		pos_1 = position(1,2);
		pos_2 = position(2, 4);
	}
});


test( "Position returns coordinates", function() {
	QUnit.equal(pos_1.getX(), 1);
	QUnit.equal(pos_1.getY(), 2);
});


test( "Position subtract", function() {
	var diff = pos_2.subtract(pos_1);
	
	QUnit.equal(diff.getX(), 1);
	QUnit.equal(diff.getY(), 2);
});


test( "Position multiply", function() {
	var pos = position(3, 4);

	var scaledPosition = pos.multiply(2);
	
	QUnit.equal(scaledPosition.getX(), 6);
	QUnit.equal(scaledPosition.getY(), 8);
	QUnit.equal(scaledPosition.norm(), 10);
});


test( "Position norm", function() {
	var pos = position(3, 4);
	
	QUnit.equal(pos.norm(), 5);
});


var startPosition;
var endPosition;

module("Event tests", {
    setup: function () {
        startPosition = position(1,2);
        endPosition = position(1,2);
    }
});


test( "Event duration", function() {
	var testEvent = event(1, "", startPosition, 0, endPosition, 2);
	
	QUnit.equal(testEvent.getDuration(), 2);
});


test( "Event reschedule", function() {
	var testEvent = event(1, "", startPosition, 0, endPosition, 2);
	
	testEvent = testEvent.reschedule(1, 2);
	
	QUnit.equal(testEvent.getDuration(), 4);
});
