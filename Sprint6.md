### Objectives ###

  * Music playback started and stopped regarding the user location
  * Facebook login from the Android
  * Creation of spots, channels and musics from the Android
  * Like a channel from the Android
  * Record sounds/voice from the Android and associate it with a spot.
  * Listen to music samples from the iTunes API or napster if the user doesn't have the music on its smartphone.
  * Full operational Web UI to create spots, channels and to upload musics or music's metadata with a facebook account.

### Resources ###

  * Start day: 3/23/2011
  * End day: 4/19/2011

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
| US32                          | Change the icon bound to a music regarding its source and availability. | See the [wiki page](US32_HowToDemo.md) | 48                              | abarreir                         | ½                               | -                                 | ![http://fabienrenaud.com/armp/star-white.png](http://fabienrenaud.com/armp/star-white.png) |
| US57                          | Update the database to be able to retrieve users that are _currently_ in a spot, to have a picture for a spot, to improve users' privacy. Then, put it online. | Download the [archive of the ARMP database](http://code.google.com/p/armp/downloads/detail?name=database.tar.xz&can=2&q=#makechanges) and open the mwb file with MySQL Workbench or look at the attached database graph. | 47                              | Fabien                           | 1                               | ½                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US56                          | Implementation of a Java applet to be able to upload easily multiple musics' metadatas throught the Web UI. | Go to [the web UI](http://fabienrenaud.com/armp/www/adm/) and try to add musics. | 46                              | Fabien                           | 10                              | 20                                | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US58                          | Improve facebook integration of the Web UI and create a real facebook login system for the standalone Web UI. | Go to [the web UI](http://fabienrenaud.com/armp/www/adm/) and login with a facebook account. | 45                              | Fabien                           | 25                              | 25                                | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US59                          | Music playback based on the user location. | See the [wiki page](US59_HowToDemo.md) | 44                              | abarreir                         | 8                               | 8                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US60                          | Add a menu item and preference pane for facebook login. | See the [wiki page](US60_HowToDemo.md) | 43                              | abarreir                         | 3                               | 2                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US61                          | Implementation of a RESTful WS creating a spot and a channel in the same time. | Read the [Create Spot and Channel WS page](WS_CreateSC.md). | 42                              | Fabien                           | 1                               | 1                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US62                          | Implementation of a RESTful WS allowing to upload a picture for a spot. | Read the [Upload Photo WS page](WS_UploadPhoto.md). | 41                              | Fabien                           | 1                               | 1                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US63                          | Implementation of RESTful WS to delete a spot, a channel or a music. | Read [Delete Spot WS page](WS_DeleteSpot.md), [Delete Channel WS page](WS_DeleteChannel.md) and [Delete Music WS page](WS_DeleteMusic.md). | 40                              | Fabien                           | 1                               | 1                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US64                          | Study of the implementation of RTMP for Android |                                        | 39                              | Mathieu                          | 4                               | 2                                 | ![http://fabienrenaud.com/armp/warning.gif](http://fabienrenaud.com/armp/warning.gif) |
| US65                          | Studies of the iTunes and Napster API  | Check [iTunes API](http://www.apple.com/itunes/affiliates/resources/documentation/itunes-store-web-service-search-api.html) and [Napster API](http://access.napster.com/docs/)| 38                              | Mathieu                          | 2                               | 1                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US66                          | Implementation of the iTunes request and playback of music preview | See the [wiki page](US65_HowToDemo.md) | 37                              | Mathieu                          | 6                               | 5                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US66                          | Implementation of recording activity   |                                        | 36                              | Mathieu                          | 4                               | -                                 | ![http://fabienrenaud.com/armp/star-gold.png](http://fabienrenaud.com/armp/star-gold.png) |
| US67                          | Implementation of the Facebook authentication on the ARMP server with a Facebook account from the Android. | See the [wiki page](US67_HowToDemo.md) | 35                              | Fabien                           | 3                               | 4                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US68                          | Fetch the standard XML responses of the ARMP server whatever the request is. Provide an object able to get string message from the ARMP server. | See the [MyDefaultHandler](http://code.google.com/p/armp/source/browse/trunk/src/com/android/armp/model/parser/MyDefaultHandler.java?spec=svn283&r=283) object and the over XML handlers of the [revision 283](http://code.google.com/p/armp/source/detail?r=283). | 34                              | Fabien                           | 1                               | 1                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US69                          | Implementation of objects sending post requests and able to send a file. | See the HttpPostRequest and HttpSendFile objects in the [ArmpApp object](http://code.google.com/p/armp/source/browse/trunk/src/com/android/armp/localized/ArmpApp.java?spec=svn283&r=283). | 33                              | Fabien                           | 1                               | 1                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US70                          | Fix bugs causing a crash of the application when listing musics or local pictures. | See code [revision 283](http://code.google.com/p/armp/source/detail?r=283). | 32                              | Fabien                           | 1                               | 1½                                | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |

### Links ###

  * [Product Backlog](ProductBacklog.md)