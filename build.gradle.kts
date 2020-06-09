import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.10"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("io.freefair.lombok") version "5.1.0"
}

group = "me.ianlibanio.npc"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    // Paper
    compileOnly(files(File(projectDir, "libs/paper.jar")))
}

tasks {
    "compileKotlin"(KotlinCompile::class) {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
    "shadowJar"(ShadowJar::class) {
        baseName = project.name
        classifier = ""
    }
}