# Get Spots #

## Action ##

  * Obtains the message corresponding to a number.

## Request ##
<table cellspacing='4' border='0'>
<blockquote><tr><td><b>URL</b></td><td><a href='http://fabienrenaud.com/armp/www/getMessage.php'>http://fabienrenaud.com/armp/www/getMessage.php</a></td></tr>
<tr><td><b>Method</b></td><td>GET</td></tr>
<tr><td><b>Authentication</b></td><td>Not Required</td></tr>
</table></blockquote>

Mandatory parameters:
  * no = _int_ ≥ 1000 The message number to fetch

Optional parameters:
  * hl = _string_ A [ISO 639-1](http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) language code

## Response ##

That WS returns a XML file with the following tree:
  * **root**
    * **message**

### Sample ###

```
<?xml version="1.0" encoding="utf-8"?>
<root>
  <message>Some parameters are missing.</message>
  <logged>1</logged>
  <status>1</status>
</root>
```

## See also ##

  * [Common/Default WS Response](WS_DefaultResponse.md)