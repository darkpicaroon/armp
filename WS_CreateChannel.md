# Create Channel #

## Actions ##

  * Creates a channel for a specified spot with a given name.

## Request ##
<table cellspacing='4' border='0'>
<blockquote><tr><td><b>URL</b></td><td><a href='http://fabienrenaud.com/armp/www/createChannel.php'>http://fabienrenaud.com/armp/www/createChannel.php</a></td></tr>
<tr><td><b>Method</b></td><td>POST</td></tr>
<tr><td><b>Authentication</b></td><td>Required.</td></tr>
</table></blockquote>

Mandatory parameters:
  * `spotId` = _int_ > 0
  * name = _string_ The name of the channel.

Optional parameters:
  * _None_

## Response ##

That WS returns a XML file with the following tree:
  * **root**
    * **channel** | id | spotId
      * _channel elements_

The channel elements are the following:
<table cellspacing='4' border='0'>
<tr><td><b>name</b></td><td><i>string</i></td><td>The name of the channel</td></tr>
<tr><td><b>user</b></td><td><i>string</i></td><td>The name of the user who created the channel</td></tr>
<tr><td><b>genre</b></td><td><i>string</i></td><td>The main genre of the channel</td></tr>
<tr><td><b>musics</b></td><td><i>int</i></td><td>The count of musics in the channel</td></tr>
<tr><td><b>creation</b></td><td><i>int</i></td><td>A unix timestamp date of the creation of the channel</td></tr>
<tr><td><b>update</b></td><td><i>int</i></td><td>A unix timestamp date of the last update of the channel</td></tr>
</table>

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

  * [Get Channels](WS_GetChannels.md)
  * [Common/Default WS Response](WS_DefaultResponse.md)