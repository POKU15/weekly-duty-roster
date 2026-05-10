plugins {
    kotlin("jvm") version "1.8.22"
    application
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("app.MainKt")
}
