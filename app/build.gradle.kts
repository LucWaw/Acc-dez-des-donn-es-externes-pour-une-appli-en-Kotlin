plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("kotlin-kapt")
  id("com.google.dagger.hilt.android")
}

android {
  namespace = "com.aura"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.aura"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }
  buildFeatures {
    viewBinding = true
  }
}

dependencies {

  implementation("com.google.dagger:hilt-android:2.44")
  kapt("com.google.dagger:hilt-android-compiler:2.44")

  runtimeOnly("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")

  // Moshi JSON Library
  implementation("com.squareup.moshi:moshi-kotlin:1.15.1")


  // Retrofit for Network Requests
  implementation("com.squareup.retrofit2:retrofit:2.11.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
  implementation("com.squareup.retrofit2:converter-moshi:2.9.0")



  implementation("androidx.core:core-ktx:1.9.0")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("com.google.android.material:material:1.8.0")
  implementation("androidx.annotation:annotation:1.6.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

// Allow references to generated code
kapt {
  correctErrorTypes = true
}