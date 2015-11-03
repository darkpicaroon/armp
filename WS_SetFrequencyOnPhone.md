# Set Frequency On Phone #

## Actions ##

  * Adds the frequency of music genre according to the ones on his phone.

## Request ##
<table cellspacing='4' border='0'>
<blockquote><tr><td><b>URL</b></td><td><a href='http://fabienrenaud.com/armp/www/setFrequencyOnPhone.php'>http://fabienrenaud.com/armp/www/setFrequencyOnPhone.php</a></td></tr>
<tr><td><b>Method</b></td><td>POST</td></tr>
<tr><td><b>Authentication</b></td><td>Required.</td></tr>
</table></blockquote>

Mandatory parameters:
  * genres = _list of strings_ Strings have to be separated with **;**
  * freqs = _list of integers_ Integers have to be separated with **;**

Optional parameters:
  * _None_

## Response ##

That WS returns the default XML response. [More...](WS_DefaultResponse.md)

## See also ##

  * [Common/Default WS Response](WS_DefaultResponse.md)