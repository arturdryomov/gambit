# Gambit

This repository contains the source code for the Gambit Android app
[available on Google Play][Google Play link].
Gambit is a really simple flashcards viewer and manager for Android.

[![Screenshot][Screenshot image]][Google Play link]

## License

* [Apache Version 2.0][Apache link]

## Building

[![Travis Status][Travis image]][Travis link]

You will need JDK 1.7+ installed.
Gradle, Android SDK and all dependencies will be downloaded automatically.

```
$ ./gradlew clean assembleDebug
```

## Acknowledgements

Gambit uses some open source libraries.

* [Bundler][Bundler link]
* [Butter Knife][Butter Knife link]
* [Commons IO][Commons IO link]
* [Cursor Utils][Cursor Utils link]
* [Dart][Dart link]
* [Floating Action Button][Floating Action Button link]
* [Material EditText][Material EditText link]
* [Otto][Otto link]
* [Seismic][Seismic link]
* [Transitions Backport][Transitions Backport link]
* [ViewPager Indicator][ViewPager Indicator link]


  [Apache link]: http://www.apache.org/licenses/LICENSE-2.0.html
  [Google Play link]: https://play.google.com/store/apps/details?id=ru.ming13.gambit
  [Travis link]: https://travis-ci.org/ming13/gambit

  [Bundler link]: https://github.com/f2prateek/bundler
  [Butter Knife link]: https://github.com/JakeWharton/butterknife
  [Commons IO link]: http://commons.apache.org/proper/commons-io
  [Cursor Utils link]: https://github.com/venmo/cursor-utils
  [Dart link]: https://github.com/f2prateek/dart
  [Floating Action Button link]: https://github.com/makovkastar/FloatingActionButton
  [Material EditText link]: https://github.com/rengwuxian/MaterialEditText
  [Otto link]: http://square.github.com/otto
  [Seismic link]: https://github.com/square/seismic
  [Transitions Backport link]: https://github.com/guerwan/TransitionsBackport
  [ViewPager Indicator link]: https://github.com/JakeWharton/ViewPagerIndicator

  [Screenshot image]: https://cloud.githubusercontent.com/assets/200401/6249966/cfe27878-b79b-11e4-8ddc-93c927d65a8c.png
  [Travis image]: https://travis-ci.org/ming13/gambit.svg?branch=master
