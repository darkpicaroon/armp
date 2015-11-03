# US25 & US26 - Connecting the client application to the server #

## Achieved tasks ##

The next step after building the base application (see [US04 results page](USO4_HowToDemo.md)) and creating a first version of the server (see [the database design](database.md), and [the requests handled by the server](ServerSideRequests.md)), the objective of these user stories was to connect the client side with the server side.

To do so, we proceeded in 5 steps:
  1. Adding a component to allow background processing.
  1. Implementing a class to handle server requests.
  1. Creating parser objects to handle server responses.
  1. Creating an object model to cast the server responses into java objects.
  1. Saving the created objects into a persistent and application-wide object.

### 1. Background processing ###

As our application deals with users' location and as the interface won't always be active in the users' handset, it is mandatory for application to be able to receive events and react to those events even when the application is not displayed. To do so, we added a [Service](http://developer.android.com/reference/android/app/Service.html) object linked to our application.

The goal of this object is to receive location updates from the location provider, and to determine if a new music should be played regarding the user's location changes. It has also consequences on the interface when it is displayed, as a change in the currently playing music usually changes UI components. See [LocalizedMusicService.java](http://code.google.com/p/armp/source/browse/trunk/src/com/android/armp/localized/LocalizedMusicService.java) for implementation details.

### 2. Handling server requests ###

Basically, our application contains two objects that need to interact with the server:
  * The map view, which displays the music spots and the user location, obviously needs to retrieve information about spots/channels/musics in order to update the UI.
  * The background service object also needs to be able to perform server requests, as it can happen that the user moves to a location whom spots/channels/musics has not been cached, for instance because the user hasn't interacted with the application for a while.

Thus, we needed an object that was accessible by both components, we thus first thought about creating a Singleton object. A more elegant way of doing such a thing with Android is to extend the [Application class](http://developer.android.com/reference/android/app/Application.html). This class enables one to maintain a global state accessible by each component of the application, and is automatically instantiated when the process of the application is first created.

By calling methods over this "global" object, the two components presented above can retrieve data avout spots/channels/musics, from the server or from the cache if the Application object has already performed a similar request (feature to be implemented later on this project). One can find implementation details by looking at the class [ArmpApp.java](http://code.google.com/p/armp/source/browse/trunk/src/com/android/armp/localized/ArmpApp.java).

### 3. Handling server responses ###

Communicating with the server isn't useful if we don't understand what it tells us. As the server responses are xml files (see [this page](ServerSideRequests.md) for their description), we implemented a [SAX](http://en.wikipedia.org/wiki/Simple_API_for_XML) parser for each kind of xml file returned by the server, which construct the java objects while reading the xml files.

### 4. Casting server responses ###

To handle each kind of response from the server, we designed three different objects:
  * [MusicSpot](http://code.google.com/p/armp/source/browse/trunk/src/com/android/armp/localized/MusicSpot.java)
  * [MusicChannel](http://code.google.com/p/armp/source/browse/trunk/src/com/android/armp/localized/MusicChannel.java)
  * [MusicItem](http://code.google.com/p/armp/source/browse/trunk/src/com/android/armp/localized/MusicItem.java)

To keep the same links between objects than the one sent by the server responses, musics associated to a channel are saved in a list in the channel object, and so are the channels associated with a spot.

### 5. Saving created objects ###

As explained before, through the use of an Application object, we can save a global state and offer global methods to each components running in the main context. As a consequence, a list of the spots retrieved from the server is stored in this object.

Further, when a caching process will be implemented, this model could change for optimization purposes.