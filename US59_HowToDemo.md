# US59 - Playback of musics regarding user location #

## Achieved tasks ##

For now, music playback was handled through the UI, when the user selected a music from a channel, without having to be in the spot the channel belong to in order to be able to play the music.

The LocalizedMusicService, which is a thread running in the background and getting the user location updates, now handles the music playback. First, it requests the application to maintain a list of the music spots that are close to the user location. Thus, each time the user move from a defined distance, this service requests the application for an update of this list of spots. As the user can enter one of these spots at any time, the WS getSpots has been updated to return the full list of channels and musics present in a spot when a certain parameter is given (see [getSpots](WS_GetSpots.md) for more details).

When the user enters a spot, this service automatically gets the first channel from the spot and ask the MediaPlaybackService to pause the current playlist and start the playback of the musics from the current channel. The playability of each music is handled by the MediaPlaybackService, which knows thanks to the isPlayable() method of a music item if it can be played on the device (because the music is present on the device, or a preview is available, or the music is a web stream).

Moreover, the UI can still have control upon the music being played on a spot. Thus, if the user is still in a spot, the user can change the channel that is being played by selecting a music in this channel. The background service will thus be asked to start the playback of this new list of musics, starting at the position the user selected.

Finally, when the user leaves a spot, the playlist that was previously loaded is restored (as well as the current position in the playlist and the position in the song that was being played).

## Further work ##

The current implementation of location-based music playback lacks a mechanism enabling the application to detect incoherent positon received from the location provider. This can cause the music to be stopped and restarted randomly, as the system can detect that the user has entered/exited a spot whereas he actually didn't move.