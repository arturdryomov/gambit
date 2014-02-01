# Gambit

This repository contains the source code for the Gambit Android app.

Gambit is a really simple flashcards viewer and manager for Android.

[![Google Play Badge][Google Play badge image]][Google Play link]
[![Screenshot][Screenshot image]][Google Play link]

## License

* [Apache Version 2.0][Apache license link]

## Building

You will need JDK 1.6, Android SDK 22 and Gradle 1.10 installed.

1. Install required Android components.

  ```
  $ android update sdk --no-ui --force --all --filter build-tools-19.0.0
  $ android update sdk --no-ui --force --all --filter android-19
  $ android update sdk --no-ui --force --all --filter extra-android-m2repository
  ```

2. Build application.

  ```
  $ gradle clean assembleDebug
  ```

## Acknowledgements

Gambit uses some open source libraries and tools:

* [Otto][Otto link]
* [Seismic][Seismic link]
* [ViewPagerIndicator][ViewPagerIndicator link]


  [Google Play badge image]: http://www.android.com/images/brand/get_it_on_play_logo_large.png
  [Screenshot image]: http://img826.imageshack.us/img826/3525/nexusy.png

  [Google Play link]: https://play.google.com/store/apps/details?id=ru.ming13.gambit
  [Apache license link]: http://www.apache.org/licenses/LICENSE-2.0.html
  [Otto link]: http://square.github.com/otto
  [Seismic link]: https://github.com/square/seismic
  [ViewPagerIndicator link]: http://viewpagerindicator.com
