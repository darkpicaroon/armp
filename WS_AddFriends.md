# Add Friends #

## Actions ##

  * Adds friends for the current user.

## Request ##
<table cellspacing='4' border='0'>
<blockquote><tr><td><b>URL</b></td><td><a href='http://fabienrenaud.com/armp/www/addFriends.php'>http://fabienrenaud.com/armp/www/addFriends.php</a></td></tr>
<tr><td><b>Method</b></td><td>POST</td></tr>
<tr><td><b>Authentication</b></td><td>Required.</td></tr>
</table></blockquote>

Mandatory parameters:
  * facebookIds = _list of integers_ Integers have to be separated with **;**
  * facebookNames = _list of strings_ Strings have to be separated with **;**

Optional parameters:
  * _None_

## Response ##

That WS returns the default XML response. [More...](WS_DefaultResponse.md)

## See also ##

  * [Common/Default WS Response](WS_DefaultResponse.md)