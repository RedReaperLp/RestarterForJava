import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.github.redreaperlp"
version = ""

repositories {
    mavenCentral()
}

dependencies {

}


tasks {
    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("")
        manifest {
            attributes(
                    "Main-Class" to "com.github.redreaperlp.Main"
            )
        }
    }
}

application {
    mainClass.set("com.github.redreaperlp.Main")
}