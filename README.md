# Gambit

This repository contains the source code for the Gambit Android app.

Gambit is a really simple flashcard viewer for Android.

![Screenshot](http://img4.imageshack.us/img4/856/shotua.png)

## Features

* Simple UI — no pasarán things that you’ll never use.
* Shuffle your cards if you want to give yourself an extra
training. Just shake your phone and it’ll be done.
* You have thousands of cards and you forgot where stopped?
No problem — Gambit remembers your current position in deck.
* Sync with “big brother” — Gambit knows how to sync with Google Drive
and will create spreadsheet you can modify easily.

## License

* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Building

The build requires Maven and the Android SDK to be installed.

The building process is very simple:

* Run `mvn -f application/pom.xml clean package` to build APK.
* Run `mvn clean install` to build application and run tests.

For correct building you should set up API keys.
Look at `application/res/xml/apis.template.xml` for details.

## Acknowledgements

Gambit uses many open source libraries and tools:

* [Android Maven plugin](https://github.com/jayway/maven-android-plugin)
* [ActionBarSherlock](https://github.com/JakeWharton/ActionBarSherlock)
* [Google APIs Client Library for Java](http://code.google.com/p/google-api-java-client/)
* [Java Excel API](http://jexcelapi.sourceforge.net/)
* [Apache Commons Lang](http://commons.apache.org/lang/)
