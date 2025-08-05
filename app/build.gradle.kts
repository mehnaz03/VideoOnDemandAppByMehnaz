plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")


}


android {
    namespace = "com.mehnaz.videoondemandapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mehnaz.videoondemandapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        compose = false
        viewBinding= true
    }
}

dependencies {

    // Core
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.2")

    // Retrofit (Networking)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp (Optional: Logging for Retrofit)
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")
    implementation("com.google.dagger:hilt-android:2.51")
    implementation("androidx.navigation:navigation-fragment:2.9.3")
    kapt("com.google.dagger:hilt-android-compiler:2.51")

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // ExoPlayer Core
    implementation ("androidx.media3:media3-exoplayer:1.8.0")

// UI components for playback controls
    implementation ("androidx.media3:media3-ui:1.8.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}
