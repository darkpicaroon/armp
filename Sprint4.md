### Objectives ###

  * Basic web server implementation to get information about spots, channels and streams
  * Basic client application implementation retrieving and displaying spots and related information.

### Resources ###

  * Start day: 03/2/2011
  * End day: 03/8/2011

### Backlog ###

<table width='100%' border='0'>
<tr>
<td width='auto'></td>
<td width='24'><img src='http://fabienrenaud.com/armp/success.gif' /></td>
<td width='60'><i>Success</i></td>
<td width='24'><img src='http://fabienrenaud.com/armp/star-gold.png' /></td>
<td width='110'><i>Work in Progress</i></td>
<td width='24'><img src='http://fabienrenaud.com/armp/star-white.png' /></td>
<td width='55'><i>Standy</i></td>
<td width='24'><img src='http://fabienrenaud.com/armp/warning.gif' /></td>
<td width='150'><i>Canceled or Postpone</i></td>
</tr>
</table>
| **[ID](LabelInformation.md)** | **[Description](LabelInformation.md)** | **[How to Demo](LabelInformation.md)** | **[Imp.](LabelInformation.md)** | **[Owner](LabelInformation.md)** | **[Est.](LabelInformation.md)** | **[Elaps.](LabelInformation.md)** | **[Status](LabelInformation.md)** |
|:------------------------------|:---------------------------------------|:---------------------------------------|:--------------------------------|:---------------------------------|:--------------------------------|:----------------------------------|:----------------------------------|
| US21                          | Database design and MySQL implementation. | Check the database page [here](http://code.google.com/p/armp/wiki/database). | 84                              | Mathieu                          | 0.5                             | 1                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US22                          | Implementation of a RESTful WS providing a XML file displaying spots based on location of the user. | getspots method page [here](http://code.google.com/p/armp/wiki/getspots) | 83                              | Mathieu                          | 5                               | 5                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US23                          | Implementation of a RESTful WS providing a XML file giving information about channels for a given spot. | getchannels method page [here](http://code.google.com/p/armp/wiki/getchannels) | 82                              | Mathieu                          | 2                               | 2                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US24                          | Implementation of a RESTful WS providing a XML file giving music/stream metadata and if it is a stream, the source of the stream. | getmusics method page [here](http://code.google.com/p/armp/wiki/getmusics) | 81                              | Mathieu                          | 2                               | 2                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US25                          | Implementation of classes related to data provided by WS (rel. [US22](Sprint4.md)-[US24](Sprint4.md)) for the client side. | See the [wiki page](US25_US26_HowToDemo.md) | 80                              | abarreir                         | 2                               | 2                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif)|
| US26                          | Implementation of a client-side class requesting WS (rel. [US22](Sprint4.md)-[US24](Sprint4.md)) and transforming results into Java objects (rel. [US25](Sprint4.md)). | See the [wiki page](US25_US26_HowToDemo.md) | 79                              | abarreir                         | 3                               | 5                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US27                          | Display spots on the Google Map view using KML files provided by the WS related to the [US22](Sprint4.md). | On the wiki page: how the KML file is put in the map view, screenshot... | 78                              | Fabien                           | 3                               | 4                                 | ![http://fabienrenaud.com/armp/warning.gif](http://fabienrenaud.com/armp/warning.gif) |
| US28                          | Display information about channels when clicking on a spot using XML files provided by the WS related to the [US23](Sprint4.md). | screenshot                             | 77                              | Fabien                           | 3                               | 8                                 | ![http://fabienrenaud.com/armp/warning.gif](http://fabienrenaud.com/armp/warning.gif) |
| US29                          | Display information about streams/musics when clicking on a channel using XML files provided by the WS related to the [US24](Sprint4.md). | screenshot                             | 76                              | Fabien                           | 3                               | 1                                 | ![http://fabienrenaud.com/armp/warning.gif](http://fabienrenaud.com/armp/warning.gif) |

### Links ###

  * [Product Backlog](ProductBacklog.md)