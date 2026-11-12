plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}
android {
    namespace = "com.prime.launcher"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.prime.launcher.mobile"
        minSdk = 26
        targetSdk = 35
        versionCode = providers.environmentVariable("GITHUB_RUN_NUMBER").orElse("1").get().toInt()
        versionName = "0.1.0"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
// Always sign mobile debug APKs with the exact persistent key restored by CI.
android.signingConfigs.create("primeStable") {
    storeFile = file(
        System.getenv("PRIME_SIGNING_KEY")
            ?: "${System.getProperty("user.home")}/.android/prime-mobile-signing.jks"
    )
    storePassword = "android"
    keyAlias = "primemobile"
    keyPassword = "android"
    storeType = "JKS"
}
android.buildTypes.getByName("debug") {
    signingConfig = android.signingConfigs.getByName("primeStable")
}

dependencies {
    implementation("androidx.webkit:webkit:1.12.1")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
