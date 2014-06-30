#race
A web based racing game

##Design
###Real time communication between client and browser (web-util modules)
Time aware communication between client and browser is implemented by periodic requests from the client sent with a specified time interval.

The server runs a clock that responds to the client requests based on the submitted data and the clock time. Clients can specify for which time interval their request is intended and receive timeout information if the reqeust is not received in the intended interval.

There are two variants implemted, for countdown timers the server runs the clock on a fixed interval length and responds with predefined data to requests that arrive after the specified count down period ellapsed, and before that with the remaining time.

For synchronized operations, for each clock time interval the server collects requests from a specified set of participants and waits with the response until either the requests from all participants arrived or the clock time interval ellapsed. After all requests where received, a new clock interval is started. Thereby the client times are synchronized to the slowest client, and the server clock time interval is variable. This could (should) have been choosen to be constant as well, synchronizing all clients to the server.

Data transfered with these requests is expected to be in JSON format. For countdown timers the response will be a JSON object that holds the remaining time in the 'remaining' property, and the timer is done when the remaining time is -1. For synchronized requests the JSON request and response hold the time interval in the 'time_count' property, and additional data in the 'data' property.

###Server api (race-web module)
Non-static requests to the server api are handled by a dispatch servlet, which dispatches the request based on a specified handler id. The handler id is provided by the server, either by a call to the dispatch servlet or encoded in the resource url.

###Game logic (race-engine modules)
For the race game itself a race track consisting of several track sections can be specified by defining the outer boundary of each section with an associated type (that currently corresponds to its maximum speed). Additionally a finish line, start grid positions and a maximum number of laps must be specified. The logic expects the current state of the player, containing it's current position and direction, as well as it's game status (active, finished or terminated). Based on the given state it calculates the resulting track sections for a given time interval. Thereby a track section consists of a start and end point with the associated start and end time. Additionally it calculates the resulting end state of the player.

The algorithm used is the following:
- Determine the ray from the current position in the current direction of the player
- Determine the section type for the current position
- Determine all intersections points of the ray with the sides of the race track polygon
- Order the intersection points by the distance from the starting point
- Iterate through the ordered interesection points, keeping track of the section type

###Build information
The project is setup with maven (maven.apache.org) and creates a war archive to be deployed to a servlet container. The command
```Shell
mvn clean install
```
, run from the "race-parent" directory, will create the war archive in your local maven repostiory under the coordinates specified in the pom.xml file in this directory.
```Shell
mvn clean test
```
will run the unit tests in all modules.

The (...somewhat non-existing) javascript unit tests can (or better could) be run ..somehow.

###Deployment
The war archive created can be deployed to a servlet container of your choice. The servlet container must, however, support asynchronouos servlets.
The start page of the applciation can be reached throght ROOT/welcome.html, where ROOT is the root url setup for the sevlet container. 
