# Get Channels #

## Actions ##

  * Obtains channels of a specified spots.
  * Default behavior will give the 10 first channels if the user is unauthentified.
  * If the user is authentified, it sorts channels according to the user's preference.


## Request ##
<table cellspacing='4' border='0'>
<blockquote><tr><td><b>URL</b></td><td><a href='http://fabienrenaud.com/armp/www/getChannels.php'>http://fabienrenaud.com/armp/www/getChannels.php</a></td></tr>
<tr><td><b>Method</b></td><td>GET</td></tr>
<tr><td><b>Authentication</b></td><td>Not required. Optional.</td></tr>
</table></blockquote>

Mandatory parameters:
  * spotId = _int_ > 0

Optional parameters:
  * start = _int_ > 0
  * limit = _int_ > 0
  * heavy = _int_ {0,1}

## Response ##

That WS returns a XML file with the following tree:
  * **root**
    * **channels**
      * `*` **channel** | id | spotId
        * _channel elements_

The channel elements are the following:
<table cellspacing='4' border='0'>
<tr><td><b>name</b></td><td><i>string</i></td><td>The name of the channel</td></tr>
<tr><td><b>user</b></td><td><i>string</i></td><td>The name of the user who created the channel</td></tr>
<tr><td><b>genre</b></td><td><i>string</i></td><td>The main genre of the channel</td></tr>
<tr><td><b>count</b></td><td><i>int</i></td><td>The number of musics in the channel</td></tr>
<tr><td><b>creation</b></td><td><i>int</i></td><td>A unix timestamp date of the creation of the channel</td></tr>
<tr><td><b>update</b></td><td><i>int</i></td><td>A unix timestamp date of the last update of the channel</td></tr>
</table>

If the heavy mode is on, the _channel elements_ will also contain a tree identical of those replied by the [Get Musics Web Service](WS_GetMusics.md) (except root).

### Samples ###

#### Heavy mode Off ####
```
<?xml version="1.0" encoding="utf-8"?>
<root>
  <channels>
    <channel id="9" spotId="1">
      <name>chan1</name>
      <user>Super User</user>
      <genre>None</genre>
      <count>2</count>
      <creation>1300717041</creation>
      <update>1300717041</update>
    </channel>
    <channel id="10" spotId="1">
      <name>chan2</name>
      <user>Super User</user>
      <genre>None</genre>
      <count>5</count>
      <creation>1300717046</creation>
      <update>1300717046</update>
    </channel>
  </channels>
  <logged>1</logged>
  <status>1</status>
</root>
```

#### Heavy mode On ####

```
<?xml version="1.0" encoding="utf-8"?>
<root>
  <channels>
    <channel id="2" spotId="1">
      <name>chan2</name>
      <user>Super User</user>
      <genre>None</genre>
      <count>2</count>
      <creation>1301136007</creation>
      <update>1301136007</update>
      <musics>
        <music id="22">
          <metadata id="22">
            <title>Back in Black</title>
            <artist>ACDC</artist>
            <album>Back</album>
            <year>1974</year>
            <track>6</track>
            <genre>Instrumental</genre>
            <duration>210</duration>
          </metadata>
        </music>
        <music id="23">
          <metadata id="24">
            <title>Jailbreak</title>
            <artist>ACDC</artist>
            <album>Jail</album>
            <year>1976</year>
            <track>1</track>
            <genre>Instrumental</genre>
            <duration>211</duration>
          </metadata>
        </music>
      </musics>
    </channel>
  </channels>
  <logged>1</logged>
  <status>1</status>
</root>
```

## See also ##
  * [Common/Default WS Response](WS_DefaultResponse.md)
  * [Old getchannels.php WS](getchannels.md)