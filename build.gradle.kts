// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("lifecycle_version", "2.9.1")
        set("room_version", "2.8.5")
        set("compose_version", "1.12.1")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.devtools.ksp") version "2.3.6" apply false
    id("androidx.room") version "2.8.5" apply false
    alias(libs.plugins.compose.compiler) apply false
    id("com.google.dagger.hilt.android") version "2.60.1" apply false

}
extra["ndkVersion"] = "28.0.12916984"
