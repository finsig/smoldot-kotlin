## Smoldot Kotlin

A Kotlin wrapper for the [smoldot](https://github.com/smol-dot/smoldot) Rust-based  Polkadot light client.


## Installation

Add the following dependency to your `build.gradle.kts`:

```kotlin
implementation("io.finsig:smoldotkotlin:0.1.1")
```

## Usage

A Chain Specification file must be provided to initialize a chain. A Chain Specification is a JSON Object that describes a Polkadot-based blockchain network.

*Example Chain Specification JSON files for Polkadot, Kusama, Rococo, and Westend can be copied for use from [/smoldotkotlin/src/androidTest/assets](https://github.com/finsig/smoldot-kotlin/tree/main/smoldotkotlin/src/androidTest/assets).*

Initialize a chain from a specification file:

```kotlin
val specification = ChainSpecification()
    .readFromAsset("file name", LocalContext.current))
    
val chain = Chain(specification)
```

Add the chain to the client to connect to the network:
```kotlin
Client.instance().add(chain)
```

RPC requests must conform to the JSON-RPC 2.0 protocol.
```kotlin
val request = JSONRPC2Request.parse(\"{\"id\":1,\"jsonrpc\":\"2.0\",\"method\":\"chain_getHeader\",\"params\":[]}")
```

To send the request:
```kotlin
Client.instance().send(request,chain)
```

Collect from the responses flow to get the response value:
```kotlin
CoroutineScope().launch {
    Client.instance().responses(chain).collect { response -> 
        // Do something with the response string
    }
}    
```

To disconnect the client from the network use:

```kotlin
Client.instance().remove(chain)
```

For additional information about usage see [reference documentation](https://finsig.github.io/smoldot-kotlin/smoldotkotlin/io.finsig.smoldotkotlin/index.html).

## Logging

You may enable logging of the smoldot Rust FFI library with a Gradle Build Configuration field (`RUST_LOG`). The library uses the Rust`env_logger` framework and levels can be set accordingly.

```gradle
android {
  ...
  buildTypes {
    debug {
        buildConfigField("String", "RUST_LOG", "\"info\"")
    }
  }
}
```

## Building locally

The following Android Studio tools are required to build locally:
* SDK Tools v34.0.0
* NDK (Side by side) v26.1.10909125
* CMake v3.22.1

### Shell Script
The NDK code is built using CMake and called from Kotlin using the Java Native Interface (JNI). The provided shell script can be used to build the static libary files for each Android ABI.

```zsh
$ zsh build_staticlibs.sh
```

### Dockerfile

A Dockerfile is provided that can build (but not test) the AAR for the project.

Note: the Dockerfile is configured to run on 64-bit architecture. If using x86 architecture, remove the `--platform` flag from the ubuntu build stage of the [Dockerfile](https://github.com/finsig/smoldot-kotlin/blob/bde451561f8c2003c184434406ebd2923fa6689f/Dockerfile#L39).

Build a Docker image from the Dockerfile:

```zsh
docker build -t "finsig:Dockerfile" .
```

Gradle Wrapper can then be invoked as follows:

```zsh
 docker run --rm -v `pwd`:/project finsig:Dockerfile bash -c 'cd /project; \
  ./gradlew smoldotkotlin:bundleReleaseAar' 
```

See [GitHub issues](https://github.com/finsig/smoldot-kotlin/issues) for notes regarding warning messages.


## Testing

Unit tests are provided in the form of instrumented tests. Regular unit tests will not work because they do not provide access to Android specific framework dependencies. These tests require an Android Virtual Device (AVD) or attached physical device, and can be run from Android Studio.


## Notes

The Dockerfile is a fork of [Docker Android Build Box](https://github.com/finsig/docker-android-build-box).

