plugins {
    kotlin("jvm") version "1.6.10"
    id("io.papermc.paperweight.userdev") version "1.3.4"
    `maven-publish`
    `java-library`
}

group = "com.github.devngho"
version = "v0.1-alpha0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}