plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.medicatiooandhealthtrackerthemain"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.medicatiooandhealthtrackerthemain"
        minSdk = 25
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
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.google.material)
    implementation(libs.firebase.inappmessaging)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    //implementation(libs.play.services.ads.api)
    val roomVersion = "2.8.4"

    implementation("androidx.room:room-runtime:${roomVersion}")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project

    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    annotationProcessor("androidx.room:room-compiler:$roomVersion")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:${roomVersion}")

    // optional - RxJava2 support for Room
    implementation("androidx.room:room-rxjava2:${roomVersion}")

    // optional - RxJava3 support for Room
    implementation("androidx.room:room-rxjava3:${roomVersion}")

    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation("androidx.room:room-guava:${roomVersion}")

    // optional - Test helpers
    testImplementation("androidx.room:room-testing:${roomVersion}")

    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:${roomVersion}")
   /* implementation(libs.room.paging)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)*/
}



