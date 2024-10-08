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

- [Getting started using CUE in Java](https://cuelang.org/docs/tutorial/get-started-cue-java/) on cuelang.org
- [Other Java+CUE guides](https://cuelang.org/search/?q=tag:%22java%20api%22) on cuelang.org

## Known Limitations

The `libcue` bindings are pregenerated for macOS/arm64,
other systems are unlikely to work.

## Issue tracking

Please raise all issues in
[the main CUE repository](https://github.com/cue-lang/cue/issues),
giving the title of the issue a `cue-api-java: ` prefix.

Raising an issue through the following link will prepopulate the issue with relevant
information, saving you time:\
[Open a `cue-api-java` issue](https://github.com/cue-lang/cue/issues/new?labels=Triage,NeedsInvestigation,language%3A%20java&title=cue-api-java:%20&body=%3C%21--%0APlease+answer+these+questions+before+submitting+your+issue.+Thanks%21%0ATo+ask+questions%2C+see+https%3A%2F%2Fgithub.com%2Fcue-lang%2Fcue%23contact.%0A--%3E%0A%0A%23%23%23+What+version+of+%60cue-api-java%60+are+you+using%3F%0A%0A%23%23%23+Does+this+issue+reproduce+with+the+latest+commit%3F%0A%0A%23%23%23+What+did+you+do%3F%0A%0A%3C%21--%0AIf+possible%2C+provide+a+recipe+for+reproducing+the+error.%0A%0AFor+advice+on+how+to+create+a+good+reproducer%2C+please+see%3A%0A%0Ahttps%3A%2F%2Fgithub.com%2Fcue-lang%2Fcue%2Fwiki%2FCreating-test-or-performance-reproducers%0A--%3E%0A%0A%0A%0A%23%23%23+What+did+you+expect+to+see%3F%0A%0A%0A%0A%23%23%23+What+did+you+see+instead%3F%0A)
