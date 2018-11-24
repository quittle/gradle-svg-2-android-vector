# SVG 2 Android Vector Plugin
A simple, Gradle plugin for automatic conversion of SVGs to Android Vector drawables during the build using [Android's official conversion tool](https://android.googlesource.com/platform/tools/base/+/master/sdk-common/src/main/java/com/android/ide/common/vectordrawable/Svg2Vector.java).

Android does not support SVG drawables out of the box because it does not implement the full SVG spec. Instead, it has
its [own XML-based, vector format](https://developer.android.com/guide/topics/graphics/vector-drawable-resources).
Android Studio contains an SVG to Android vector conversion tool named
[Vector Asset Studio](https://developer.android.com/studio/write/vector-asset-studio). That tool is a one-time tool that
converts the SVGs to the new format, which is then checked in. This means the source of truth for the drawable is no
longer the general SVG file a designer might produce, but the converted file. To ensure the source and the artifact stay
in sync, this plugin allows you to check in just the SVG and produce the Android Vector at build time.

## Consuming

### build.gradle (*Coming Soon*)
```groovy
plugins {
    id 'com.quittle.svg-2-android-vector' version '0.0.0'
}
```

### Resouce layout
Put your SVGs in the `raw` Android resource folder and consume them as if they were in the drawable. You *will not* be able to consume the original SVGs as their names would conflict.

```java
src
└── main
    ├── java
    │   └── com
    │       └── example
    │           └── project
    │               └── MyActivity.java // mView.setBackground(R.drawable.ic_foobar);
    └── res
        └── raw
            └── ic_foobar.svg
```

## Building
```sh
# Build the plugin and run static analysis tools
./gradlew -p svg-2-android-vector

# Publish it the local maven
./gradlew -p svg-2-android-vector publishToMavenLocal

# Apply the plugin locally in a test project that validates it runs.
./gradlew -p example-android-project assemble
```

## Publishing
**TODO**: Use Travis CI and publish to the Gradle plugin repository
