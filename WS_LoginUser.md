# Login User #

## Actions ##

  * Creates a user or updates its _last connection_ field in the database.
  * Initializes the php session.

## Request ##
<table cellspacing='4' border='0'>
<blockquote><tr><td><b>URL</b></td><td><a href='http://fabienrenaud.com/armp/www/loginUser.php'>http://fabienrenaud.com/armp/www/loginUser.php</a></td></tr>
<tr><td><b>Method</b></td><td>POST</td></tr>
<tr><td><b>Authentication</b></td><td>Not Required. Optional</td></tr>
</table></blockquote>

Mandatory parameters:
  * udid = _string_ Represents the Unique Device ID of the user's phone
  * facebookId = 0 ≤ _int_ ≤ 4294967295
  * facebookName = _string_

Optional parameters:
  * _None_

## Response ##

That WS returns the default XML response. [More...](WS_DefaultResponse.md)

## See also ##

  * [Common/Default WS Response](WS_DefaultResponse.md)