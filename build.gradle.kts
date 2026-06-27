plugins {
    id("java")
}

group = "me.actuallysoheil.plugin.smp"
version = "0.0.2-PREVIEW"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    val lombok = "org.projectlombok:lombok:1.18.46"
    compileOnly(lombok)
    annotationProcessor(lombok)

    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")

    compileOnly("org.mongodb:mongodb-driver-sync:5.8.0")
}