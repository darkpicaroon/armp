# Create Music #

## Actions ##

  * Creates/adds a music for a specified channel.

## Request ##
<table cellspacing='4' border='0'>
<blockquote><tr><td><b>URL</b></td><td><a href='http://fabienrenaud.com/armp/www/createMusic.php'>http://fabienrenaud.com/armp/www/createMusic.php</a></td></tr>
<tr><td><b>Method</b></td><td>POST</td></tr>
<tr><td><b>Authentication</b></td><td>Required.</td></tr>
</table></blockquote>

Mandatory parameters:
  * `channelId` = _int_ > 0
  * title = _string_ The name of the channel.
  * duration = _int_ The music's duration in seconds.

Optional parameters:
  * source = _string_ The source of the channel, if it is a stream.
  * artist = _string_
  * album = _string_
  * genre = _string_ The genre of the music.
  * year = _int_
  * track = _string_

## Response ##

That WS returns an xml file with the following tree:
  * **root**
    * **music** | id
      * **source** _(optional)_
      * **metadata** | id
        * **metadata elements

The metadata elements are the following:**<table cellspacing='4' border='0'>
<tr><td><b>title</b></td><td><i>string</i></td><td>The title of the music</td></tr>
<tr><td><b>artist</b></td><td><i>string</i></td><td>The artist/band of the music</td></tr>
<tr><td><b>album</b></td><td><i>string</i></td><td>The album name of the music</td></tr>
<tr><td><b>year</b></td><td><i>int</i></td><td>The release year of the album</td></tr>
<tr><td><b>track</b></td><td><i>int</i></td><td>The track number of the music</td></tr>
<tr><td><b>genre</b></td><td><i>string</i></td><td>The genre of the music</td></tr>
<tr><td><b>duration</b></td><td><i>int</i></td><td>The duration, in seconds, of the music</td></tr>

</table>

#### Sample ####

```
<?xml version="1.0" encoding="utf-8"?>
<root>
  <music id="4">
    <metadata id="1">
      <title>Run Run Run</title>
      <artist>Phoenix</artist>
      <album>Alphabetical</album>
      <year>2011</year>
      <track>1</track>
      <genre>Instrumental</genre>
      <duration>200</duration>
    </metadata>
  </music>
  <logged>1</logged>
  <status>1</status>
</root>
```

### Sample ###

```
<?xml version="1.0" encoding="utf-8"?>
<root>
  <channel id="9" spotId="1">
    <name>New Chan</name>
    <user>MyUser</user>
    <genre>None</genre>
    <musics>0</musics>
    <creation>1300717041</creation>
    <update>1300717041</update>
  </channel>
  <logged>1</logged>
  <status>1</status>
</root>
```

## See also ##

  * [Get Musics](WS_GetMusics.md)
  * [Common/Default WS Response](WS_DefaultResponse.md)