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

## Contributing

Pull requests are welcome! But there is one notice. If you would want to
test synchronization you should modify `application/res/values/keys.xml` file
to access Google API. After that you should run command
`git update-index --skip-worktree application/res/values/keys.xml` to prevent
adding private information to repository.
