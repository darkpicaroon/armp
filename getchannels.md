# Request #
This request is used to get the channels associated with a spot.

# Parameters #
<br><i>spot_id</i>: the id referencing the spot.</br>
<br><i>nb_channels</i>: the number of channels to send back in response.</br>
<br><i>start_id</i>: the identifier of the channel from which to start (in case some channels have already be sent to the client application.</br>

# Response #

The response is an xml file containing all the information needed for the client use such as: the spot id, the name of the channel, its creation and last update time and the number of music associated to it.

# Sample #