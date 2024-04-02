# cue-api-java: use CUE from Java

This is a work in progress, expect constant churn and breakage.

## Prerequisites

Java 22 is required for build,
[libcue](https://github.com/cue-lang/libcue) is required at runtime.

## Building

Run `mvn build`.
Currently, the path to `libcue` is hardcoded in `pom.xml`,
you might need to change that.

## Using

TODO.

## Known Limitations

The `libcue` bindings are pregenerated for macOS/arm64,
other systems are unlikely to work.

## Issue tracking

Please raise all issues in
[the main CUE repository](https://github.com/cue-lang/cue/issues),
giving the title of the issue a `cue-api-java: ` prefix.

