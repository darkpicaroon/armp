# Get Spots #

## Actions ##

  * Obtains all spots in an area.
  * If the zoom is low, the response will group spots.

## Request ##
<table cellspacing='4' border='0'>
<blockquote><tr><td><b>URL</b></td><td><a href='http://fabienrenaud.com/armp/www/getSpots.php'>http://fabienrenaud.com/armp/www/getSpots.php</a></td></tr>
<tr><td><b>Method</b></td><td>GET</td></tr>
<tr><td><b>Authentication</b></td><td>Not Required</td></tr>
</table></blockquote>

Mandatory parameters:
  * latne = _double_
  * lngne = _double_
  * latsw = _double_
  * lngsw = _double_
  * zoom = 0 ≤ _int_ ≤ 20

Optional parameters:
  * heavy = _int_ {0,1}

## Response ##

That WS returns a XML file with the following tree:
  * **root**
    * **spots**
      * `*` **spot** | id
        * _spot elements_

The spot elements are the following:
<table cellspacing='4' border='0'>
<tr><td><b>name</b></td><td><i>string</i></td><td>The name of the spot</td></tr>
<tr><td><b>user</b></td><td><i>string</i></td><td>The name of the user who created the spot</td></tr>
<tr><td><b>latitude</b></td><td><i>double</i></td><td>The latitude of the spot</td></tr>
<tr><td><b>longitude</b></td><td><i>double</i></td><td>The longitude of the spot</td></tr>
<tr><td><b>color</b></td><td><i>string</i></td><td>An HTML color code for the color of the spot</td></tr>
<tr><td><b>radius</b></td><td><i>int</i></td><td>The radius, in meters, of the spot</td></tr>
<tr><td><b>picture</b></td><td><i>string</i></td><td>The url of the picture of the spot.</td></tr>
<tr><td><b>creation</b></td><td><i>int</i></td><td>A unix timestamp date of the creation of the spot</td></tr>
<tr><td><b>update</b></td><td><i>int</i></td><td>A unix timestamp date of the last update of the spot</td></tr>
</table>

If the heavy mode is on, the _spot elements_ will also contain a tree identical of those replied by the [Get Channels Web Service](WS_GetChannels.md) with the same heavy option enabled.

#### Samples ####

#### Heavy mode Off ####

```
<?xml version="1.0" encoding="utf-8"?>
<root>
  <spots>
    <spot id="4">
      <name>IUT</name>
      <user>Super User</user>
      <latitude>49.0995475230457</latitude>
      <longitude>6.21734931382753</longitude>
      <color>7a00c8</color>
      <radius>150</radius>
      <picture>img/iut.jpeg</picture>
      <creation>1300714653</creation>
      <update>1300714653</update>
    </spot>
    <spot id="1">
      <name>GTL</name>
      <user>Super User</user>
      <latitude>49.1020271699258</latitude>
      <longitude>6.2151284447632</longitude>
      <color>7a00c8</color>
      <radius>150</radius>
      <picture>img/gtl.jpg</picture>
      <creation>1300713451</creation>
      <update>1300713451</update>
    </spot>
    <spot id="3">
      <name>Stadium</name>
      <user>Super User</user>
      <latitude>49.1021255103074</latitude>
      <longitude>6.21948435220338</longitude>
      <color>7a00c8</color>
      <radius>150</radius>
      <picture>img/stadium.png</picture>
      <creation>1300714553</creation>
      <update>1300714553</update>
    </spot>
  </spots>
  <logged>1</logged>
  <status>1</status>
</root>
```

#### Heavy mode On ####

```
<?xml version="1.0" encoding="utf-8"?>
<root>
  <spots>
    <spot id="4">
      <name>IUT</name>
      <user>Super User</user>
      <latitude>49.0995475230457</latitude>
      <longitude>6.21734931382753</longitude>
      <color>7a00c8</color>
      <radius>150</radius>
      <picture>img/spot19.jpeg</picture>
      <creation>1300714653</creation>
      <update>1300714653</update>
      <channels>
        <channel id="7" spotId="4">
          <name>chan2</name>
          <user>Super User</user>
          <genre>None</genre>
          <count>1</count>
          <creation>1301136047</creation>
          <update>1301136047</update>
          <musics>
            <music id="22">
              <metadata id="22">
                <title>Black Halo</title>
                <artist>Kamelot</artist>
                <album>Black Halo</album>
                <year>2005</year>
                <track>3</track>
                <genre>Metal</genre>
                <duration>185</duration>
              </metadata>
            </music>
          </musics>
        </channel>
      </channels>
    </spot>
    <spot id="1">
      <name>GTL</name>
      <user>Super User</user>
      <latitude>49.1020271699258</latitude>
      <longitude>6.2151284447632</longitude>
      <color>7a00c8</color>
      <radius>150</radius>
      <picture>img/spot1.png</picture>
      <creation>1300713451</creation>
      <update>1300713451</update>
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
    </spot>
  </spots>
  <logged>1</logged>
  <status>1</status>
</root>
```

## See also ##

  * [Common/Default WS Response](WS_DefaultResponse.md)
  * [Old getspots.php WS](getspots.md)