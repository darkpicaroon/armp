# US67: Android×Facebook authentication on the ARMP server #

## Objective ##

The Facebook authentication on the ARMP server only works from a regular web browser before the User Story 67.

The aim of that User Story is to accomplish the same basic operations than a web browser to authenticate a user with his Facebook account on the ARMP server and from the Android.

Remember that authentication is:
  * **not required** to see spots/channels/musics or listen to it.
  * **required** to create spots/channels/musics, like a channel, get personalized content...

## Principle ##

The authentication on the ARMP server always starts with the [LoginUser WS](WS_LoginUser.md). That WS is waiting for a facebook cookie whose name have to be `fbs_<FB APP ID>` and which is suppose to contain at least two fields:
  * access\_token: the Facebook token of the user
  * sig: a hash of _all_ the values in the cookie

Once that cookie received on the server, that one takes the access\_token and use the [Facebook Graph API](http://developers.facebook.com/docs/reference/api/) to verify if the user is the right one and has authorize the application.

Then the server creates a PHP session and sends back a PHPSESSID value. That PHPSESSID has a short span life, generally 30 minutes, and has to be used each time the user want to use authenticated features on the server to avoid a complete Facebook authentication to happen again.

## HTTP Headers ##

By analyzing what a regular web browser sends a receives, we can easily find out what to implement.

### `LoginUser` HTTP request header ###
```
POST /armp/www/loginUser.php HTTP/1.1
User-Agent: Opera/9.80 (X11; Linux x86_64; U; en) Presto/2.8.131 Version/11.10
Host: www.fabienrenaud.com
Accept-Language: en-US,en;q=0.9
Accept-Encoding: gzip, deflate
Referer: http://www.fabienrenaud.com/armp/www/adm/
Cookie: 90plan=R3276318907; fbs_193388247366826="access_token=XYZ&expires=TIMESTAMP&secret=UVW&session_key=ABC&sig=MD5_HASH&uid=FACEBOOK_UID";
Connection: Keep-Alive
Content-Length: 0
Accept: */*
X-Requested-With: XMLHttpRequest
Content-Type: text/xml; charset=utf-8
Content-Transfer-Encoding: binary
```

### `LoginUser` HTTP response header ###
```
HTTP/1.0 200 OK
Set-Cookie: PHPSESSID=7gklpkf5uv8gpivn9l3cotmor3; 90plan=R3276318907; path=/; expires=Wed, 20-Apr-2011 03:16:07 GMT
Date: Sun, 17 Apr 2011 14:55:40 GMT
Server: Apache/2.2.X (OVH)
X-Powered-By: PHP/5.3.5
Expires: Thu, 19 Nov 1981 08:52:00 GMT
Cache-Control: no-store, no-cache, must-revalidate, post-check=0, pre-check=0
Pragma: no-cache
Vary: Accept-Encoding
Content-Encoding: gzip
Content-Length: 93
Content-Type: text/html
X-Cache: MISS from taimatsu1.rez
X-Cache-Lookup: MISS from taimatsu1.rez:3128
Via: 1.1 taimatsu1.rez:3128 (squid/2.7.STABLE3)
Connection: keep-alive
```

## Java/Android implementation ##

Thus, to authenticate a Facebook user on the ARMP server from the Android, the only thing to do is just to be able to send a Facebook cookie to the ARMP server once this one is authenticated on Facebook and then to be able to create a PHPSESSID to transmit in each HTTP request done to the ARMP server.

Hence the code implemented in the [code revision 283](http://code.google.com/p/armp/source/detail?r=283):

### The `loginUser` method ###
```
private void loginUser() {
  if (mFacebook == null || !mFacebook.isSessionValid())
    return;

  List<NameValuePair> params = new ArrayList<NameValuePair>();
  params.add(new BasicNameValuePair("pseudo", ""));
  if (connectionAttempt < 2) {
    connectionAttempt++;
    Thread t = new Thread(new HttpPostRequest(LOGIN_REQ_T, LOGIN_REQ,
                    params, new MyDefaultHandler()));
    t.start();
  }
}
```

### The `updateFacebookCookie` method ###
```
public void updateFacebookCookie() {
  String name = "fbs_" + mFacebook.getAppId();
  if (mFacebook == null || !mFacebook.isSessionValid()) {
    removeCookie(name);
  } else {
    String payload = "access_token=" + mFacebook.getAccessToken();
    payload += "expires=" + mFacebook.getAccessExpires();

    String value = "access_token=" + mFacebook.getAccessToken();
    value += "&expires=" + mFacebook.getAccessExpires();
    value += "&sig=" + md5(payload);

    updateCookie(name, value);
    loginUser();
  }
}
```

#### The MD5 method ####
```
private String md5(String s) {
  try {
    // Create MD5 Hash
    MessageDigest digest = java.security.MessageDigest
        .getInstance("MD5");
    digest.update(s.getBytes());
    byte messageDigest[] = digest.digest();


    // Create Hex String
    StringBuffer hexString = new StringBuffer();
    for (int i = 0; i < messageDigest.length; i++)
      hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
    return hexString.toString();


  } catch (NoSuchAlgorithmException e) {
    Log.e(TAG, e.getMessage());
  }
  return "";
}
```

### The `saveCookies` method ###

That method has to be called each time a HTTP response is received from the ARMP server.
```
private void saveCookies(HttpResponse r) {
  synchronized (cookies) {
    Header[] headers = r.getHeaders("Set-Cookie");
    for (Header h : headers) {
      String[] cc = h.getValue().split(";");
      for (String c : cc) {
        String[] nv = c.split("=");
        if (nv.length == 2) {
          updateCookie(nv[0], nv[1]);
        }
      }
    }
  }
}
```

### The cookie utility methods ###

#### Get ####
```
private HttpHeader getCookie(String name) {
  String n = name.toLowerCase();
  for (HttpHeader c : cookies)
    if (c.getName().toLowerCase().equals(n))
      return c;
  return null;
}
```

#### Update ####
```
private void updateCookie(String name, String value) {
  HttpHeader co = getCookie(name);
  if (co == null)
    cookies.add(new HttpHeader(name, value));
  else
    co.setValue(value);
}
```

#### Remove ####
```
private boolean removeCookie(String name) {
  String n = name.toLowerCase();
  for (int i = 0; i < cookies.size(); i++) {
    if (cookies.get(i).getName().toLowerCase().equals(n)) {
      cookies.remove(i);
      return true;
    }
  }
  return false;
}
```

### The `setHeaders` method ###

That method has to be called each time just before a `HttpRequest` (`HttpPost` or `HttpGet`) is sent by a `AndroidHttpClient`.

```
private void setHeaders(HttpRequest r) {
  synchronized (cookies) {
    StringBuilder sb = new StringBuilder();
    for (HttpHeader c : cookies) {
      if (sb.length() > 0)
        sb.append("; ");
      sb.append(c.getName() + "=" + c.getValue());
    }
    if (sb.length() > 0) {
      r.addHeader("Cookie", sb.toString());
    }
  }
}
```