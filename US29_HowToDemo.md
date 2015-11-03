# US29 - Displaying music items #

## Achieved tasks ##

The process of retrieving and displaying the musics associated with a given channel is slightly the same than the one used to retrieve and display channels. The difference here is that musics can have different sources:
  * Local music: With the metadata embedded in the server response, we perform a search in the user's phone memory, with a helper class (see [MusicSourceSolver](http://code.google.com/p/armp/source/browse/trunk/src/com/android/armp/localized/MusicSourceSolver.java) for implementation details). Regarding the search results, there are two possibilities:
    1. The user has the music in his device. In this case, the MusicSourceSolver will embed in the music object the uri of the music.
    1. The user doesn't have the music in his device. We are looking for ways to provide him with a 30 seconds preview of the music, and a way to buy it.
  * Distant music: The returned music comes with an url where the music/stream can be retrieved.

Regarding the music source, we will display a different icon to help the user knowing which song he can play in a future version.

## Results ##

This is the first version of this view, which will certainly evolve during the project, for better user-friendliness and interactivity.

The icon displayed in front of each music is temporarly the application icon, until we create icons to represent the music source.

![http://armp.googlecode.com/files/MusicsDisplayV1.png](http://armp.googlecode.com/files/MusicsDisplayV1.png)