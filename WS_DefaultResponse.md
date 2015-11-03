# Common/Default Web Service Response #

## General structure ##
Each WS will return a XML file. That XML file can contains many elements defining the purpose of the WS.

Every WS returns the same XML structure which is the following:

```
<?xml version="1.0" encoding="utf-8"?>
<root>
  <!-- Other elements defined by the WS -->
  <logged>xxx</logged>
  <status>xxx</status>
</root>
```

## The _logged_ element ##

The _logged_ element contains an integer which his value is 0 or 1:
  * **1** means _User connected_
  * **0** means _User not connected_

## The _status_ element ##

The _status_ element contrains an integer which can take several values:
  * **0** means _Nothing has been done, or, flag not modified_
  * **1** means _Operation successfully accomplished_
  * ...

A lot of values exist for that element. For each value is associated a string message that can be fetched using the [getMessage WS](WS_GetMessage.md).

## See also ##

  * [Get Message](WS_GetMessage.md)