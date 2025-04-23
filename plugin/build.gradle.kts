plugins {
    id("java")
    id("com.github.hierynomus.license") version "0.16.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16"
}

group = "com.guy7cc.s2nbt"
version = "0.1.0"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "enginehub"
        url = uri("https://maven.enginehub.org/repo/")
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.10")
}

val targetJavaVersion = 21
java {
    sourceCompatibility = JavaVersion.toVersion(targetJavaVersion)
    targetCompatibility = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < JavaVersion.toVersion(targetJavaVersion)) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

license {
    header = file("$rootDir/config/license/header.txt")
    exclude("**/*")
    include("**/*.java")
    mapping("java", "SLASHSTAR_STYLE")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible()) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.jar {
    archiveBaseName.set("StructureToNBT")
}