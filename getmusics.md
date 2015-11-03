# Request #
This request is used to get the musics associated with a specified channel.


# Parameters #
<br><i>channel_id</i>: the id referencing the channel.</br>
<br><i>nb_channels</i>: the number of musics to send back in response.</br>
<br><i>start_id</i>: the identifier of the music from which to start (in case some musics have already be sent to the client application.</br>

# Response #

There are two different possible response corresponding to the type of source:

<ul>
<li>local: In that case the server sends back the metadata corresponding to the music to enable the armp player to find that music within the client personnal folder.<br>
</li>
<li>distante: If the stream of music is stored a server the response contains the url to play that stream.<br>
</li>
</ul>

# Sample #