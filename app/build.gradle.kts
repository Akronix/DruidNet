plugins {
//    kotlin("plugin.serialization") version "1.9.0"
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp")
    id("androidx.room")
}

android {
    namespace = "org.druidanet.druidnet"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.druidanet.druidnet"
        minSdk = 26
        targetSdk = 35
        versionCode = 11
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildTypes {

        // Bad idea; the updates has to have the same build variant every time.
//        create("beta") {
//
//            initWith(getByName("debug"))
//
////            applicationIdSuffix = ".beta"
//            versionNameSuffix = "-beta"
//
//            ndk.debugSymbolLevel = "FULL"
//
//            isDebuggable = false
//
//            // Enables code shrinking, obfuscation, and optimization for only
//            // your project's release build type. Make sure to use a build
//            // variant with `isDebuggable=false`.
//            isMinifyEnabled = false
//
//            // Enables resource shrinking, which is performed by the
//            // Android Gradle plugin.
//            isShrinkResources = false
//
//        }

        release {

            ndk.debugSymbolLevel = "FULL"

            isDebuggable = false

            // Enables code shrinking, obfuscation, and optimization for only
            // your project's release build type. Make sure to use a build
            // variant with `isDebuggable=false`.
            isMinifyEnabled = true

            // Enables resource shrinking, which is performed by the
            // Android Gradle plugin.
            isShrinkResources = true

            proguardFiles(
                // Includes the default ProGuard rules files that are packaged with
                // the Android Gradle plugin. To learn more, go to the section about
                // R8 configuration files.
                getDefaultProguardFile("proguard-android-optimize.txt"),
                // Includes a local, custom Proguard rules file
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    ndkVersion = rootProject.extra["ndkVersion"] as String
    room {
        schemaDirectory("$projectDir/schemas")
    }

}


dependencies {

    // Serializable
//    implementation(libs.kotlinx.serialization.json)

    // Markdown
    implementation(libs.multiplatform.markdown.renderer)
    implementation(libs.multiplatform.markdown.renderer.m3)
    implementation(libs.multiplatform.markdown.renderer.android)

    // SplashScreen
    implementation(libs.androidx.core.splashscreen)

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${rootProject.extra["lifecycle_version"]}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${rootProject.extra["lifecycle_version"]}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${rootProject.extra["lifecycle_version"]}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:${rootProject.extra["lifecycle_version"]}")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:${rootProject.extra["lifecycle_version"]}")

    implementation("androidx.compose.runtime:runtime:${rootProject.extra["compose_version"]}")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.core.ktx)

    ksp("androidx.room:room-compiler:${rootProject.extra["room_version"]}")
    implementation(libs.androidx.room.ktx)

    // Telephoto lib
    implementation("me.saket.telephoto:zoomable:0.13.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
