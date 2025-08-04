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
        maven {
            name = "GitHubPackagesHuiDuTest"
            url = uri("https://maven.pkg.github.com/ms03001620/HuiDuTest") // 确保这里的 URL 正确
            credentials {
                username = System.getenv("GITHUB_USER")
                password = System.getenv("GITHUB_TOKEN_FOR_PACKAGES")
            }
        }
    }
}

rootProject.name = "HuiDuTest"
include(":app")
include(":core")
