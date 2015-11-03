# Music Web Services Comparison #
## Summary ##

| **Name** | **Version** | **Platforms** | **Languages/Framework** | **AMS**`*` | **Services** | **Request** | **Response** | **Doc** | **Registration** | **Terms** |
|:---------|:------------|:--------------|:------------------------|:-----------|:-------------|:------------|:-------------|:--------|:-----------------|:----------|
| Deezer   | 3.0         | Windows, Mac OS X, Linux |                         | Yes        | Search, Lookup Artist/Album/Lookup Track| HTTP/GET    | XML, JSON, PHP | [Here](http://www.deezer.com/fr/developers/#developers/simpleapi/). | [Here](http://www.deezer.com/fr/api#api/new.php). |           |
| Grooveshark | 1.0         | Windows, Mac OS X, Linux |                         | No         | [See detailed services here](http://developers.grooveshark.com/).| HTTP/GET    | JSON-encoded objects | [Here](http://developers.grooveshark.com/). | Need web service key. |           |
| Last.FM  | 2.0         | Windows, Mac OS X, Linux | Java, C++, .NET, Python, Ruby, PHP, Actionscript, C#, Perl, Javascript, Objective-C | No         | Search, album/artiste browsing, chart, radio, [see more](http://www.lastfm.fr/api/intro). | HTTP/GET, XML-RPC | Text, XML, JSON, XSPF, RSS | [Here](http://www.lastfm.fr/api/intro). | [Here](http://www.lastfm.fr/api/account). Requires an account! | [Here](http://www.lastfm.fr/api/tos). |
| Soundcloud | Java wrapper: 1.0.0 beta ; C wrapper: 0.6.0 |               | Java, C, Python, Actionscript 3, PHP, Ruby, Cocoa, Javascript | No         | Authentication, Search for tracks/users/groups/playlists, upload of music, add or delete comments | HTTP/GET    | XML, JSON    | [Here](http://github.com/soundcloud/api/wiki/) | [Here](http://soundcloud.com/you/apps/new). Requires an account. | [Community Guidelines](http://soundcloud.com/community-guidelines), [Terms of Use](http://soundcloud.com/terms-of-use), [Trademark Guidelines](http://soundcloud.com/developers/policies#trademark), [Privacy Policy](http://soundcloud.com/pages/privacy) |
| Spotify  | 0.0.6       | Windows, Mac OS X, Linux | C                       | No         | Search, Artist/Album browsing, Session/User/Image/Playlist/Toplist handling | C call      | C type       | [Here](http://developer.spotify.com/en/libspotify/docs/) | [Here](https://developer.spotify.com/en/libspotify/application-key/). Requires a Premium account first! | [Here](http://developer.spotify.com/en/libspotify/terms-of-use/) |

_`*`AMS: Access to music streams_

## Details ##
### Deezer ###

Deezer proposes a very light API ([simpleapi](http://www.deezer.com/fr/#developers/simpleapi/)), with few methods :
  * Search
  * Lookup artist, album and tracks.

A stronger API seems to exist but is not released yet see: [Deezer API](http://www.deezer.com/fr/#developers/api/) (only reachable by changing the url).

Those API are only available by submitting our projects to Deezer and it looks like their answer are not instant.


### Grooveshark ###

Grooveshark API documentation is slight. Many methods are referenced but with no clear uses of them.

A public APi also exists: [here](http://apishark.com/). There are more information on how to use the different methods and it's free of charge
but the main problem is that it is unofficial see it's [Terms of Service](http://apishark.com/API_TOS.pdf).

### Last.FM ###

LastFM provides us an API than can be used as well on desktop applications and or mobile devices. The documentation gathers
a big number of methods and is welled structured.

It is required to be authenticated to ask for the API and as said on the terms of service
'you must be a registered User to have access to, retrieve and use the Last.fm Data'.

### Pandora ###

  * Not allowed for non US residents.
  * [Apparently no API](http://www.soatothecloud.com/2009/08/sorry-no-pandora-api-here-folks.html).

### Soundcloud ###

Soundcloud provides a strong API, samples and supports many languages. The Soundcloud API is based on RESTful Web Services and communicates through HTTP/GET requests and XML or JSON responses.

Many wrappers are available:
  * [Java wrapper](http://code.google.com/p/soundcloudapi-java/): latest version is 1.0.0 beta and the library is under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).
  * [C wrapper](http://wiki.github.com/DaveGamble/soundcloud-c-api-wrapper)
and also Python, PHP, Actionscript 3, Cocoa, Ruby wrappers.
A [widget Javascript API](http://wiki.github.com/soundcloud/Widget-JS-API) is also available.

Soundcloud provides the following services:
  * authentication
  * search for tracks/users/groups/playlists
  * upload music files
  * add or delete comments

_No information found yet about how to access to a music stream from Soundcloud. The API seems to provide only music information._

#### Links ####

  * [SoundCloud Developer Zone](http://soundcloud.com/developers)
  * [SoundCloud API Wiki](http://github.com/soundcloud/api/wiki/)
  * [SoundCloud Developer Policies](http://soundcloud.com/developers/policies)

### Spotify ###

Spotify only provides a C API package, called _libspotify_. That API is available for:
  * Windows
  * Mac OS X (x86, x86\_64, PowerPC)
  * Linux (x86, x86\_64)
and requires a Premium Accound to apply for an application key.

[Libspotify terms of use](http://developer.spotify.com/en/libspotify/terms-of-use/) does **not approve** _any mobile wireless handset or any other internet-enabled device that is designed to be handheld_.

#### Links ####

  * [Official Web Site](http://developer.spotify.com/en/libspotify/overview/)
  * [Official Documentation](http://developer.spotify.com/en/libspotify/docs/)
  * [Apply an application key](https://developer.spotify.com/en/libspotify/application-key/)