# Upload Sound #

## Actions ##

  * Upload a sound on the server for the specified channel.

## Request ##
<table cellspacing='4' border='0'>
<blockquote><tr><td><b>URL</b></td><td><a href='http://fabienrenaud.com/armp/www/uploadSound.php'>http://fabienrenaud.com/armp/www/uploadSound.php</a></td></tr>
<tr><td><b>Method</b></td><td>POST</td></tr>
<tr><td><b>Authentication</b></td><td>Required.</td></tr>
</table></blockquote>

Mandatory parameters:
  * sound = _binary data_ The uploaded file.

Optional parameters:
  * name = _string_ The name of the spot.

## Response ##

That WS returns a XML file with the following tree:
  * **root**
    * **message** The url where the picture has been stored.
    * **logged** _See [Common/Default WS Response](WS_DefaultResponse.md)_
    * **status** _See [Common/Default WS Response](WS_DefaultResponse.md)_

#### Sample ####

```
<?xml version="1.0" encoding="utf-8"?>
<root>
  <message>snd/sound.wav</message>
  <logged>1</logged>
  <status>1</status>
</root>
```

## See also ##

  * [Common/Default WS Response](WS_DefaultResponse.md)