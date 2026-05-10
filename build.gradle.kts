plugins {
    kotlin("multiplatform") version "1.9.24"
    id("org.jetbrains.compose") version "1.6.1"
    id("com.android.application") version "8.2.0"
}

repositories {
    mavenCentral()
    google()
}

kotlin {
    jvmToolchain(17)

    jvm("desktop")
    
    androidTarget()
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    wasmJs {
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("androidx.activity:activity-compose:1.8.1")
                implementation("androidx.appcompat:appcompat:1.6.1")
            }
        }

        val iosMain by getting {
            dependencies {
            }
        }

        val wasmJsMain by getting {
            dependencies {
            }
        }
    }
}

android {
    namespace = "com.dunkwafpu.roster"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.dunkwafpu.roster"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.desktop {
    application {
        mainClass = "app.MainKt"
    }
}
