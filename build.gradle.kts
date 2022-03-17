plugins {
    kotlin("jvm") version "1.6.10"
    id("io.papermc.paperweight.userdev") version "1.3.5"
    `maven-publish`
}

group = "com.github.devngho"
version = "v0.1-alpha8"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    paperDevBundle("1.18.2-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    reobfJar{
        inputJar.set(file(project.buildDir.absolutePath + File.separator + "/libs/${project.name}-${project.version}.jar"))
        outputJar.set(file(project.buildDir.absolutePath + File.separator + "/libs/${project.name}-${project.version}.jar"))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}