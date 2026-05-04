plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp) //

    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    //id("com.google.gms.google-services")
}

android {
    namespace = "com.example.frontendzmabt"
    compileSdk =36


    defaultConfig {
        applicationId = "com.example.frontendzmabt"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String","BACKEND_API_URL","\"${project.findProperty("BACKEND_API_URL")}\"")
        buildConfigField("String","GOOGLE_CLIENT_ID","\"${project.findProperty("GOOGLE_CLIENT_ID")}\"")
        buildConfigField("String","MAPS_API_KEY","\"${project.findProperty("MAPS_API_KEY")}\"")
        buildConfigField("String","API_VERSION","\"${project.findProperty("API_VERSION")}\"")
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
    buildFeatures {
        buildConfig=true
        compose = true
    }
    buildToolsVersion = "34.0.0"
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.junit.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.core.ktx)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.gson)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.java.jwt)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.kotlinx.coroutines.test)
        implementation(libs.androidx.junit.v130)
        implementation(libs.androidx.runner)
        implementation(libs.androidx.espresso.core.v370)
        implementation(libs.kotlinx.coroutines.test)
    implementation(libs.androidx.paging.compose)
    implementation(libs.maps.compose)
    implementation(libs.coil.compose)
    implementation(libs.okhttp)
    implementation(libs.socket.io.client)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    ksp(libs.room.compiler)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)        //
    implementation("com.google.firebase:firebase-messaging:25.0.1")

    implementation(platform("com.google.firebase:firebase-bom:34.12.0"))
    implementation("com.google.firebase:firebase-analytics")
}