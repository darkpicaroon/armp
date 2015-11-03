# US28 - Displaying music channels #

## Achieved tasks ##

As multiple channels can be associated with a spot, we naturally decided to display the channels in a list view. When the user selects a spot from the map view, the activity retrieves the channels associated with the spot from the server, and displays the channels view when the response is received.

Displaying this view on top of the map view is achieved by using a [ViewFlipper](http://developer.android.com/reference/android/widget/ViewFlipper.html), which enables us to switch from a view to another one, and even to perform advanced animations between these views. For now, a fade in/fade out animation has been implemented, but it might change in the future.

## Results ##

This is the first version of this view, which will certainly evolve during the project, for better user-friendliness and interactivity.

The icon displayed in front of each channel is temporarly the application icon, until we find relevant icons to represent different channels..

![http://armp.googlecode.com/files/ChannelsDisplayV1.png](http://armp.googlecode.com/files/ChannelsDisplayV1.png)