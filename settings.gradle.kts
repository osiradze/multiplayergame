pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")


rootProject.name = "MultiplayerGame"
include(":app")
include(":game")
include(":game:glcore")
include(":game:objects")
include(":game:objects:planets")
include(":game:objects:core")
include(":game:objects:player")
include(":game:objects:asteroids")
include(":game:objects:explosion")
include(":game:objects:evilplanets")
include(":game:objects:stars")
include(":game:objects:enemy")
