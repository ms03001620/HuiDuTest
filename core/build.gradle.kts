plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

group = "com.example"      // Maven 坐标的 groupId
version = "1.0.0"         // Artifact 的 version

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = project.group.toString()
                artifactId = "core"    // 可以自定义为 core 或其他
                version = project.version.toString()

                from(components["release"])
            }
        }

        repositories {
            // 发布到 ~/.m2/repository
            mavenLocal()
            // 可选：发布到本地 build 目录中的 repo
            maven {
                name = "localBuildRepo"
                url = uri(layout.buildDirectory.dir("repo"))
            }
        }
    }
}

//./gradlew :core:publishReleasePublicationToMavenLocal
//# 或者直接：
//./gradlew :core:publishToMavenLocal
//发布完成后，你会在 ~/.m2/repository/com/example/core/1.0.0/ 看到 .aar 和 .pom 文件（可选还有 .module）。

/*
以下是实际执行时的命令行logs

-----------

links@linksdeMacBook-Air HuiDuTest % ./gradlew :core:publishToMavenLocal

        Welcome to Gradle 8.5!

Here are the highlights of this release:
- Support for running on Java 21
- Faster first use with Kotlin DSL
        - Improved error and warning messages

For more details see https://docs.gradle.org/8.5/release-notes.html

Starting a Gradle Daemon, 1 incompatible Daemon could not be reused, use --status for details

> Task :core:releaseSourcesJar
Encountered duplicate path "main/com/example/core/SystemUIAccessor.kt" during copy operation configured with DuplicatesStrategy.WARN
Encountered duplicate path "main/com/example/core/SystemUIManagement.kt" during copy operation configured with DuplicatesStrategy.WARN

> Task :core:compileReleaseKotlin
w: file:///Users/links/AndroidStudioProjects/HuiDuTest/core/src/main/java/com/example/core/SystemUIManagement.kt:24:33 'clearDeviceOwnerApp(String!): Unit' is deprecated. Deprecated in Java

BUILD SUCCESSFUL in 1m 16s
        28 actionable tasks: 28 executed

-------------
执行成功了

执行成功后 app项目即需要引入core的 application 或其他的 项目
可以在本的里maven库里找到core包

1.添加仓库地址
settings.gradle.kts（或对应配置）
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

2.在模块（如 app）build.gradle.kts

dependencies {
    implementation("com.example:core:1.0.0")
}
*/


android {
    namespace = "com.example.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}