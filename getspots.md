# Request #
This request is used to get the spots near the client application.


# Parameters #
<br><i>lat</i>: The latitude of the user.</br>
<br><i>long</i>: The longitude of the user.</br>


# Response #
<br>The response given by the server is an XML type file under the following structure.</br>




&lt;Folder&gt;


> 

&lt;name&gt;



&lt;/name&gt;


> 

&lt;visibility&gt;



&lt;/visibility&gt;


> 

&lt;Placemark&gt;


> > 

&lt;Color&gt;



&lt;/Color&gt;


> > 

&lt;Radius&gt;



&lt;/Radius&gt;


> > 

&lt;Point&gt;


> > > 

&lt;coordinates&gt;



&lt;/coordinates&gt;



> > 

&lt;/Point&gt;



> 

&lt;/Placemark&gt;




&lt;/Folder&gt;



# Sample #

_request example_ = 'http://localhost/armp-dev/getspots.php?long=6.218146957908431&lat=49.10159292113388'



&lt;Folder&gt;


> 

&lt;name&gt;

KML Circle Generator Output

&lt;/name&gt;


> 

&lt;visibility&gt;

1

&lt;/visibility&gt;


> 

&lt;Placemark id=3 &gt;


> > 

&lt;Color&gt;

ff0400ff

&lt;/Color&gt;


> > 

&lt;Radius&gt;

1.2

&lt;/Radius&gt;


> > 

&lt;Point&gt;


> > > 

&lt;coordinates&gt;

6.218146957908431, 49.10159292113388, 0

&lt;/coordinates&gt;



> > 

&lt;/Point&gt;



> 

&lt;/Placemark&gt;




&lt;/Folder&gt;

