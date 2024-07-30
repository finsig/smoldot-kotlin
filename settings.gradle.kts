pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven ("https://mvnrepository.com/artifact/com.thetransactioncompany/jsonrpc2-base")
    }
}

rootProject.name = "smoldot-kotlin"
include(":smoldotkotlin")
include(":smoldotkotlinexample")
