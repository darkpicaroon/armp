# Get Musics #

## Actions ##

  * Obtains musics metadata, or music sources for a specified channel.

## Request ##
<table cellspacing='4' border='0'>
<blockquote><tr><td><b>URL</b></td><td><a href='http://fabienrenaud.com/armp/www/getMusics.php'>http://fabienrenaud.com/armp/www/getMusics.php</a></td></tr>
<tr><td><b>Method</b></td><td>GET</td></tr>
<tr><td><b>Authentication</b></td><td>Not required. Optional.</td></tr>
</table></blockquote>

Mandatory parameters:
  * channelId = _int_ > 0

Optional parameters:
  * start = _int_ > 0
  * limit = _int_ > 0

## Response ##
That WS returns an xml file with the following tree:
  * **root**
    * **musics**
      * `*` **music** | id
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
  <musics>
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
    <music id="5">
      <metadata id="2">
        <title>Heart Skipped a Beat</title>
        <artist>The xx</artist>
        <album>xx</album>
        <year>2011</year>
        <track>2</track>
        <genre>Instrumental</genre>
        <duration>250</duration>
      </metadata>
    </music>
  </musics>
  <logged>1</logged>
  <status>1</status>
</root>
```

## See also ##

  * [Common/Default WS Response](WS_DefaultResponse.md)
  * [Old getmusic.php WS](getmusics.md)