### Objectives ###

  * Find alternatives to OpenAL.
  * Study and first implementation of the sound spatialization.

### Resources ###

  * Start day: 02/16/2011
  * End day: 02/22/2011

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
| US19                          | Try to play an OGG file or a MP3 file from Android using the OpenAL. | Play an OGG or MP3 file from ARMP with the OpenAL. | 90                              | Fabien                           | 5                               | 8                                 |  ![http://fabienrenaud.com/armp/warning.gif](http://fabienrenaud.com/armp/warning.gif) |
| US10                          | Find an alternative to OpenAL to play spatialized music on Android. | The API 9 of Android (v2.3.1) supports OpenSL ES which provides 3D audio effects and spatialized music features. | 89                              | abarreir                         | 2                               | 2                                 |  ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US11                          | Build, test and understand the OpenSL ES sample (_native-audio_) provided by the NDK. | Run it from eclipse on Android.        | 88                              | All                              | 3                               | 3                                 |  ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US12                          | Adapt the OpenSL ES sample (cf. [US11](Sprint3.md)) and reduce the code to the necessary parts to use it with ARMP. | Play a music file from ARMP and with OpenSL ES. | 87                              | All                              | 1                               | 2                                 | ![http://fabienrenaud.com/armp/success.gif](http://fabienrenaud.com/armp/success.gif) |
| US20                          | Adapt the _Simple 3D_ example from the [OpenSL ES reference](http://www.khronos.org/registry/sles/specs/OpenSL_ES_Specification_1.1.pdf) (page 526) to bring 3D effects to a music played from ARMP. | Play a music with ARMP. If the 3D effect sample is enabled, the music looks like turning around the listener. | 86                              | All                              | 2                               | 6                                 | ![http://fabienrenaud.com/armp/warning.gif](http://fabienrenaud.com/armp/warning.gif) |
| US13                          | Orientate the music with the Android compass. |                                        | 85                              | All                              | 5                               | 0                                 | ![http://fabienrenaud.com/armp/warning.gif](http://fabienrenaud.com/armp/warning.gif) |
### Links ###

  * [Product Backlog](ProductBacklog.md)