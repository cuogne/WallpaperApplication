import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "com.cuogne.wallpaperapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.cuogne.wallpaperapplication"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        android.buildFeatures.buildConfig = true

        buildConfigField("String", "unsplash_api_key_access", properties.getProperty("unsplash_api_key_access"))
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
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("com.squareup.retrofit2:retrofit:3.0.0") // retrofit for call api
    implementation("com.squareup.retrofit2:converter-gson:3.0.0") // gson converter for change json -> kotlin obj
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2") // coroutines for async task
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0") // using lifecycle
    implementation("com.google.android.material:material:1.13.0") // material 3 ui design of google
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0") // pull to refresh
    implementation("io.coil-kt.coil3:coil:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")
    implementation("androidx.core:core-ktx:1.12.0")
}