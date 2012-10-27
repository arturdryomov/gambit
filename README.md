# Gambit

This repository contains the source code for the Gambit Android app.

Gambit is a really simple flashcards viewer and manager for Android.

[![Google Play Badge][Google Play badge image]][Google Play link]
[![Screenshot][Screenshot image]][Google Play link]

## License

* [Apache Version 2.0][Apache license link]

## Building

The build requires Maven and the Android SDK to be installed.

The building process is very simple:

* Run `mvn -f application/pom.xml clean package` to build APK.
* Run `mvn clean install` to build application and run tests.

For correct building you should set up API keys.
Look at `application/res/xml/apis.template.xml` for details.

## Acknowledgements

Gambit uses some open source libraries and tools:

* [Android Maven plugin][Android Maven plugin link]
* [ActionBarSherlock][ActionBarSherlock link]
* [Apache Commons Lang][Apache Commons Lang link]
* [Seismic][Seismic link]


  [Google Play badge image]: http://www.android.com/images/brand/get_it_on_play_logo_large.png
  [Screenshot image]: http://img607.imageshack.us/img607/5830/shotkf.png

  [Google Play link]: https://play.google.com/store/apps/details?id=ru.ming13.gambit
  [Apache license link]: http://www.apache.org/licenses/LICENSE-2.0.html
  [Android Maven plugin link]: https://code.google.com/p/maven-android-plugin
  [ActionBarSherlock link]: http://actionbarsherlock.com
  [Apache Commons Lang link]: http://commons.apache.org/lang
  [Seismic link]: https://github.com/square/seismic
