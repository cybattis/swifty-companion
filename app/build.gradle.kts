import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val properties = Properties().apply {
    rootProject.file("local.properties").reader().use(::load)
}

android {
    namespace = "com.cybattis.swiftycompanion"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cybattis.swiftycompanion"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders += mapOf("appAuthRedirectScheme" to "com.cybattis.swiftycompanion:/oauth2redirect")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "APP_UID", properties["APP_UID"].toString())
            buildConfigField("String", "APP_SECRET", properties["APP_SECRET"].toString())
        }
        debug {
            buildConfigField("String", "APP_UID", properties["APP_UID"].toString())
            buildConfigField("String", "APP_SECRET", properties["APP_SECRET"].toString())
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.legacy.support.v4)
    implementation(libs.appauth)
    implementation(libs.webkit)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}