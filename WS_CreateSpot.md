# Create Spot #

## Actions ##

  * Creates a spot at the given location with the specified name.

## Request ##
<table cellspacing='4' border='0'>
<blockquote><tr><td><b>URL</b></td><td><a href='http://fabienrenaud.com/armp/www/createSpot.php'>http://fabienrenaud.com/armp/www/createSpot.php</a></td></tr>
<tr><td><b>Method</b></td><td>POST</td></tr>
<tr><td><b>Authentication</b></td><td>Required.</td></tr>
</table></blockquote>

Mandatory parameters:
  * lat = _double_ The latitude of the spot to create.
  * lng = _double_ The longitude of the spot to create.
  * name = _string_ The name of the spot.
  * color = _string_ An RGB hexadecimal value corresponding to the color of the spot.

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


#### Sample ####

```
<?xml version="1.0" encoding="utf-8"?>
<root>
  <spot id="4">
    <name>New spot</name>
    <user>iUser</user>
    <latitude>49.0995475230457</latitude>
    <longitude>6.21734931382753</longitude>
    <color>#7a00c8</color>
    <radius>150</radius>
    <picture>img/mySpot.jpg</picture>
    <creation>1300714653</creation>
    <update>1300714653</update>
  </spot>
  <logged>1</logged>
  <status>1</status>
</root>
```

## See also ##

  * [Get Spots](WS_GetSpots.md)
  * [Common/Default WS Response](WS_DefaultResponse.md)