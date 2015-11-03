### Objectives ###

  * Porting OpenAL to Android and use it from the Stock Music Player.

### Resources ###

  * Start day: 02/9/2011
  * End day: 02/15/2011

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
| US14                          | Read and understand the Stock Media Player original source code (cf.  [US03](Sprint1.md)) by trying to add a _Now Playing_ panel at the bottom of the Map View of ARMP (cf. [US04](Sprint1.md)). | Look at the onCreate method of the LocalizedMusicActivity.java file and its associated layout. | 96                              | Fabien, Mathieu                  | 8                               | 8                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US08                          | Download of the OpenAL and build with the NDK. | Follow the beginning of [these instructions](http://pielot.org/category/ndk-2/) | 95                              | All                              | 3                               | 3                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US15                          | Play a short music with the OpenAL on an ARMP event. | Play a WAV file by following the end of the [pielot tutorial](http://pielot.org/category/ndk-2/). | 94                              | All                              | 1                               | 1                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US16                          | Keep playing a background music with the OpenAL after ARMP has been exited. | _It is the default behavior of OpenAL._ | 93                              | All                              | 2                               | 0                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US17                          | Bind the ARMP GUI to the music played by the OpenAL by displaying track information such as _title_, _artist_, _duration_... | _Canceled because of only WAV files._| 92                              | All                              | 5                               | 0                                 | ![http://fabienrenaud.com/armp/warning.gif](http://fabienrenaud.com/armp/warning.gif) |
| US09                          | Implementation of web hosted music playback with the OpenAL. | Play a web hosted music with OpenAL on Android | 91                              | All                              | 2                               | 0                                 | ![http://fabienrenaud.com/armp/warning.gif](http://fabienrenaud.com/armp/warning.gif) |


### Links ###

  * [Product Backlog](ProductBacklog.md)