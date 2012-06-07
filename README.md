# Gambit

Gambit is a really simple flashcards viewer for Android.

## Features

* Simple UI — no pasarán things that you’ll never use.
* Shuffle your cards if you want to give yourself an extra
training. Do you want it? Just shake your phone and it’ll be done.
* There are thousands of cards and you’d forgot where did you stop?
No problema — Gambit remembers your current position in deck.

## Building

The build requires Maven and the Android SDK to be installed.

Building process is very simple:

* `mvn -f application/pom.xml clean package` command builds application APK that you could find in `application/target` directory.
* `mvn clean install` will build application and run tests.
