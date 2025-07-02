// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("lifecycle_version", "2.9.1")
        set("room_version", "2.7.1")
        set("compose_version", "1.8.2")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.devtools.ksp") version "2.1.21-2.0.2" apply false
    id("androidx.room") version "2.7.1" apply false
    alias(libs.plugins.compose.compiler) apply false
}
val ndkVersion by extra("28.0.12916984")
