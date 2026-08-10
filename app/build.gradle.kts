import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")

}


val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.kiturk3.recipevault"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.kiturk3.recipevault"
        minSdk = 24
        targetSdk = 36
        versionCode = 4
        versionName = "1.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "com.kiturk3.recipevault.RecipeVaultTestRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(localProperties["KEYSTORE_PATH"] as? String ?: "")
            storePassword = localProperties["KEYSTORE_PASSWORD"] as? String ?: ""
            keyAlias = localProperties["KEY_ALIAS"] as? String ?: "recipevault"
            keyPassword = localProperties["KEY_PASSWORD"] as? String ?: ""
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ""
            versionNameSuffix = "-debug"
            isDebuggable = true
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    debugImplementation(libs.androidx.ui.tooling)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(platform(libs.okhttp.bom))

    // OkHttp logging (invaluable for debugging network calls)
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    //Room Database
    val roomVersion = "2.7.1"
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx.v271)  // Flow support
    ksp(libs.androidx.room.compiler)

    // Firebase BOM — manages all Firebase library versions together
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    // Google Sign-In
    implementation(libs.play.services.auth)
    implementation("androidx.credentials:credentials:1.6.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Coil for image loading
    implementation("io.coil-kt.coil3:coil-compose:3.5.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.5.0")

    implementation(libs.kotlinx.coroutines.play.services)

    //shimmer effect
    implementation("com.valentinilk.shimmer:compose-shimmer:1.5.0")

    //testing libraries
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)

}