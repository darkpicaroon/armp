# US27 - Displaying music spots #

## Achieved tasks ##

To display music spots, the application first needs to retrieve them. As described in the [US26](US25_US26_HowToDemo.md), the UI is able to request the server for such an object. To do so, we have extended the [MapView](http://code.google.com/android/add-ons/google-apis/reference/com/google/android/maps/MapView.html) class to enable it to provide information to the Activity about the area currently displayed. This [SmartMapView object](http://code.google.com/p/armp/source/browse/trunk/src/com/android/armp/localized/SmartMapView.java) listen to the user's gestures on the view and notify the activity whenever he has stopped moving around the map, giving it the area currently being displayed.

With such an information, the activity is able to request the server for spots in this area (through the application object), and display overlays on the map representing the returned spots (if any).

The displayed overlay consist of an icon representing the center of the spot, plus a circle showing the spot listening area. On a click on a spot, the activity will change its main view and display the channels associated with this spot, as described further in the [US28](US28_HowToDemo.md).

## Results ##

This is the first version of this view, which will certainly evolve during the project, for better user-friendliness and interactivity.

![http://armp.googlecode.com/files/SpotsDisplayV1.png](http://armp.googlecode.com/files/SpotsDisplayV1.png)