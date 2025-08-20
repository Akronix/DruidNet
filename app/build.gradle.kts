plugins {
    kotlin("plugin.serialization") version "1.9.10"
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp")
    id("androidx.room")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "org.druidanet.druidnet"
    compileSdk = 36

    defaultConfig {
        applicationId = "org.druidanet.druidnet"
        minSdk = 26
        targetSdk = 36
        versionCode = 17
        versionName = "1.4.0"

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

    // WorkManager dependency
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    // Coil
//    implementation("io.coil-kt.coil3:coil-compose:3.1.0")
//    implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")

    // Serializable
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    // Retrofit
    implementation(libs.retrofit)
    implementation("com.squareup.retrofit2:converter-scalars:2.11.0")

    // Retrofit with Kotlin serialization Converter
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

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
    implementation(libs.androidx.datastore.preferences)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.core.ktx)
    debugImplementation(libs.ui.tooling)

    ksp("androidx.room:room-compiler:${rootProject.extra["room_version"]}")
    implementation(libs.androidx.room.ktx)

    // Telephoto lib
    implementation(libs.zoomable)

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
