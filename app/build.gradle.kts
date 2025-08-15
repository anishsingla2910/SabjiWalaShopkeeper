plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.prototypes.sabjiwalashopkeeper"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.prototypes.sabjiwalashopkeeper"
        minSdk = 29
        targetSdk = 36
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation (libs.glide)
    implementation(libs.firebase.ui.storage)
    implementation(libs.play.services.maps)
    annotationProcessor (libs.compiler)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    implementation(libs.firebase.ui.firestore)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}