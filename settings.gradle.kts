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
    }
}

rootProject.name = "DeSponsor"

include(
    ":app",
    ":core:model",
    ":core:data",
    ":core:player",
    ":core:designsystem",
    ":feature:home",
    ":feature:player",
    ":feature:explore",
    ":feature:detail",
    ":feature:settings",
)
