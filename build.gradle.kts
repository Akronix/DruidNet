// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("lifecycle_version", "2.8.4")
        set("room_version", "2.6.1")
        set("compose_version", "1.6.8")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
    id("androidx.room") version "2.6.1" apply false
}
val ndkVersion by extra("28.0.12433566 rc1")
