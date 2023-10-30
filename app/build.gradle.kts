plugins {
    id("com.android.application")
}

android {
    namespace = "com.alanstar.iotraspberry"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.alanstar.iotraspberry"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
//            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86_64")
        }
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

    implementation ("com.github.xuexiangjys:XUI:1.2.1")
    implementation("com.github.getActivity:Toaster:12.5")
    implementation("com.github.getActivity:XXPermissions:18.5")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.android.volley:volley:1.2.1")
    debugImplementation("io.github.didi.dokit:dokitx:3.7.11")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    releaseImplementation("io.github.didi.dokit:dokitx-no-op:3.7.11")
}