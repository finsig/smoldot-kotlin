## Smoldot Kotlin

A Kotlin wrapper for the [smoldot](https://github.com/smol-dot/smoldot) Rust-based  Polkadot light client.


## Installation

Smoldot Kotlin is an Android Archive Library (AAR) and has been published to [Maven Central](
https://central.sonatype.com/artifact/io.finsig/smoldotkotlin).

Add the following to the dependencies section of your `build.gradle.kts`:
```kotlin
implementation("io.finsig:smoldotkotlin:0.1.1")
```



The Android Studio project provided contains an example app with a local library module dependency for demonstration purposes.

Instructions on how to build the AAR module are provided in the **Building Locally** section of this document.


## Usage

A Chain Specification file must be provided to initialize a chain. A Chain Specification is a JSON Object that describes a Polkadot-based blockchain network.

*Example Chain Specification JSON files for Polkadot, Kusama, Rococo, and Westend can be copied for use from [/smoldotkotlin/src/androidTest/assets](https://github.com/finsig/smoldot-kotlin/smoldotkotlin/src/androidTest/assets).*

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

There is a build_smoldotlibs.sh script in the repo which can be used to build the static C++ Rust FFI library files for inclusion in the AAR.


```zsh
$ zsh build_smoldotlibs.sh
```

A Dockerfile is provided to build the AAR for the project.

Note: the Dockerfile is configured to run on 64-bit architecture. If using x86 architecture, remove the `--platform` flag from the Dockerfile.


Build a Docker image from the Dockerfile:

```zsh
docker build -t "finsig:Dockerfile" .
```

Gradle Wrapper can then be invoked as follows:

```zsh
 docker run --rm -v \
  `pwd`:/project \
  finsig:Dockerfile bash -c 'cd /project; \
  ./gradlew smoldotkotlin:bundleReleaseAar'  \
  --platform=linux/amd64
```

See [GitHub issues](https://github.com/finsig/smoldot-kotlin/issues) for notes regarding warning messages.


## Testing

Instrumentation tests are used to test the NDK based functionality using the Android framework APIs.


## Notes

The Dockerfile is a fork of [Docker Android Build Box](https://github.com/finsig/docker-android-build-box).
