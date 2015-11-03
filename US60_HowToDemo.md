# US60 - Menu items, preferences pane and Facebook integration #

## Achieved tasks ##

A great aspect of our application is that it enables people to share location-based music with people. The experience is even better when the user can share information with his friends. We thus have integrated Facebook in our application.

To do so, three steps have been followed:

  * Creation of a preferences menu item to offer options to the user. Creating contextual menus with Android is a straightforward process, as it's just a matter of defining the items in an xml file and inflating the menu view on the main activity of our application. As our application displays various views (maps, channels and spots), we also needed to be able to display different menu items regarding the view being displayed. This has been easily done by changing the visibility property of these items regarding the current view.
  * Creation of a preferences interface. The user needs to be able to easily tweak the application thanks to preferences. The preferences interface currently enables the user to connect to Facebook, but will in the future provide him with more options. Again, creating a preferences interface is straightforward with Android. This interface is displayed when the user clicks on the menu item described before.
  * Connection to Facebook. The Facebook SDK for Android has been used to enable the use of users' information in our application. The process described [here](http://developers.facebook.com/docs/guides/mobile/#android) has been followed to declare our application to Facebook and to retrieve information from the user.

## Results ##

The menu displayed in the map view:

![http://armp.googlecode.com/files/MenuMapV1.png](http://armp.googlecode.com/files/MenuMapV1.png)

The menu displayed in the channels view:

![http://armp.googlecode.com/files/MenuChannelsV1.png](http://armp.googlecode.com/files/MenuChannelsV1.png)

The preferences interface:

![http://armp.googlecode.com/files/PreferencesV1.png](http://armp.googlecode.com/files/PreferencesV1.png)

