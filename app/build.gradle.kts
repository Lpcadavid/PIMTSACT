plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.santaellafinal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.santaellafinal"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation ("com.google.firebase:firebase-auth:21.1.0") // o la versión más reciente
    implementation ("com.google.firebase:firebase-auth:21.1.0") // Para la autenticación de Firebase
    implementation("com.github.bumptech.glide:glide:4.15.1") // Agregar Glide aquí
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1") // Para usar Glide en Android


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}