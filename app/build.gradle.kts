import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")

}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream ->
        localProperties.load(stream)
    }
}

val omdbApiKey = localProperties.getProperty("OMDB_API_KEY") ?: "default_api_key"



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

            buildConfigField("String", "OMDB_API_KEY", "\"$omdbApiKey\"")

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
        buildConfig = true
        compose = false
        viewBinding= true

    }
}

dependencies {

    // Core Android libraries
    implementation("androidx.core:core-ktx:1.16.0") // Kotlin extensions for Android core APIs
    implementation("androidx.appcompat:appcompat:1.7.1") // Backward-compatible UI components
    implementation("com.google.android.material:material:1.12.0") // Material design components
    implementation("androidx.constraintlayout:constraintlayout:2.2.1") // Layout manager for flexible UI design

    // Lifecycle components for MVVM pattern
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.2") // LiveData with Kotlin coroutines support
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.2") // ViewModel with Kotlin extensions

    // Networking libraries
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Retrofit core for REST API calls
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Gson converter for Retrofit to parse JSON

    // OkHttp client with logging interceptor for network logging
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

    // Navigation component libraries for fragment navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.3") // Navigation for fragments with Kotlin support
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7") // Navigation UI helpers with Kotlin support

    // Paging library for efficient data pagination
    implementation("androidx.paging:paging-common-android:3.3.6")
    implementation("androidx.paging:paging-runtime-ktx:3.3.6")
    implementation("androidx.activity:activity:1.10.1") // Paging with Kotlin coroutines

    // Hilt for dependency injection
    kapt("com.google.dagger:hilt-android-compiler:2.51") // Compiler for Hilt DI

    // Image loading library Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0") // Glide annotation processor

    // ExoPlayer for media playback
    implementation("androidx.media3:media3-exoplayer:1.8.0") // Core ExoPlayer library
    implementation("androidx.media3:media3-exoplayer-hls:1.8.0") // HLS streaming support
    implementation("androidx.media3:media3-ui:1.8.0") // UI components for ExoPlayer

    // Hilt dependency injection core (duplicate with above? You might want to keep one version)
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.50")

    // Facebook Shimmer library for loading placeholders
    implementation("com.facebook.shimmer:shimmer:0.5.0")


    // Testing libraries
    testImplementation("junit:junit:4.13.2") // Unit testing framework
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3") // Coroutine testing helpers
    testImplementation("io.mockk:mockk:1.13.7") // Mocking library for Kotlin
    testImplementation("androidx.arch.core:core-testing:2.2.0") // Architecture components testing helpers
}

