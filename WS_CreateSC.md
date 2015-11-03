# Create Spot and Channel #

## Actions ##

  * Creates a spot at the given location with the specified name and creates its first channel.

## Request ##
<table cellspacing='4' border='0'>
<blockquote><tr><td><b>URL</b></td><td><a href='http://fabienrenaud.com/armp/www/createSC.php'>http://fabienrenaud.com/armp/www/createSC.php</a></td></tr>
<tr><td><b>Method</b></td><td>POST</td></tr>
<tr><td><b>Authentication</b></td><td>Required.</td></tr>
</table></blockquote>

Mandatory parameters:
  * lat = _double_ The latitude of the spot to create.
  * lng = _double_ The longitude of the spot to create.
  * spotName = _string_ The name of the spot.
  * color = _string_ An RGB hexadecimal value corresponding to the color of the spot.
  * channelName = _string_ The name of the channel.

Optional parameters:
  * radius = _int_ The radius of the spot, in meters.
  * picture = _string_ The url to the picture.

## Response ##

That WS returns a XML file with the following tree:
  * **root**
    * **spot** | id
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

The spot elements will also contain a tree identical of those replied by the [Get Channels Web Service](WS_GetChannels.md) with the heavy option disabled. That sub-tree will just contain one node, the node of the first channel of the spot.

#### Sample ####

```
<?xml version="1.0" encoding="utf-8"?>
<root>
  <spots>
    <spot id="8">
      <name>first spot</name>
      <user>Fabien Renaud</user>
      <latitude>49.11613</latitude>
      <longitude>6.1727710000000116</longitude>
      <color>006497</color>
      <radius>251</radius>
      <picture>img/first spot.jpg</picture>
      <creation>1301307789</creation>
      <update>1301307789</update>
    </spot>
    <channels>
      <channel id="16" spotId="8">
        <name>my Channel</name>
        <user>Fabien Renaud</user>
        <genre>None</genre>
        <count>0</count>
        <creation>1301307789</creation>
        <update>1301307789</update>
      </channel>
    </channels>
  </spots>
  <logged>1</logged>
  <status>1</status>
</root>
```

## See also ##

  * [Create Spot](WS_CreateSpot.md)
  * [Create Channel](WS_CreateChannel.md)
  * [Get Spots](WS_GetSpots.md)
  * [Get Channels](WS_GetChannels.md)
  * [Common/Default WS Response](WS_DefaultResponse.md)