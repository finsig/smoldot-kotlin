import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.dokka") version "1.9.20"
    id("maven-publish")
    id("signing")
}

android {
    namespace = "io.finsig.smoldotkotlin"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        externalNativeBuild {
            cmake {
                arguments += listOf("-DANDROID_TOOLCHAIN=clang")
            }
        }
        ndk {
            abiFilters += listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        aarMetadata {
            minCompileSdk = 24
        }

        consumerProguardFiles("proguard-rules.pro")
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "RUST_LOG", "\"info\"")

            externalNativeBuild {
                cmake {
                    arguments += "-DCMAKE_BUILD_TYPE=DEBUG"
                    cppFlags += "-DBUILD_DEBUG"
                }
            }
        }
        release {
            buildConfigField("String", "RUST_LOG", "\"off\"")

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            externalNativeBuild {
                cmake {
                    arguments += "-DCMAKE_BUILD_TYPE=RELEASE"
                    cppFlags += "-DBUILD_RELEASE"
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    publishing {
        multipleVariants("smoldotkotlin") {
            includeBuildTypeValues("debug", "release")
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("com.thetransactioncompany:jsonrpc2-base:2.1.1")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.test:monitor:1.7.1")
    androidTestImplementation("androidx.test:runner:1.6.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
}

publishing {
    publications {
        afterEvaluate {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "io.finsig"
                artifactId = "smoldotkotlin"
                version = "0.1.1"

                pom {
                    packaging = "aar"
                    name.set("${groupId}:${artifactId}")
                    description.set("A Kotlin wrapper for the smoldot light client for Polkadot-based blockchains.")
                    url.set("https://github.com/finsig/smoldot-kotlin")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            name.set("Steven Boynes")
                            email.set("steve@finsig.io")
                            organization.set("Finsig")
                            organizationUrl.set("https://finsig.io")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/finsig/smoldot-kotlin.git")
                        developerConnection.set("scm:git:ssh://github.com:finsig/smoldot-kotlin.git")
                        url.set("https://github.com/finsig/smoldot-kotlin/")
                    }
                }
            }
        }
    }

    repositories {
        maven {
            url = uri("build/repo")
        }
    }
}

signing {
    useGpgCmd()
    afterEvaluate {
        sign(publishing.publications)
    }
}

apply(plugin = "org.jetbrains.dokka")

