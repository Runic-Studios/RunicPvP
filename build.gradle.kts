plugins {
    `java-library`
    `maven-publish`
}

group = "com.runicrealms.plugin"
version = "1.0-SNAPSHOT"

dependencies {
    compileOnly(commonLibs.paper)
    compileOnly(commonLibs.mythicmobs)
    compileOnly(commonLibs.acf)
    compileOnly(commonLibs.taskchain)
    compileOnly(commonLibs.springdatamongodb)
    compileOnly(commonLibs.mongodbdrivercore)
    compileOnly(commonLibs.mongodbdriversync)
    compileOnly(commonLibs.jedis)
    compileOnly(project(":Projects:Core"))
    compileOnly(project(":Projects:Items"))
    compileOnly(project(":Projects:Professions"))
    compileOnly(project(":Projects:Common"))
    compileOnly(project(":Projects:Database"))
    compileOnly(commonLibs.holographicdisplays)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.runicrealms.plugin"
            artifactId = "pvp"
            version = "1.0-SNAPSHOT"
            from(components["java"])
        }
    }
}

tasks.register("wrapper")
tasks.register("prepareKotlinBuildScriptModel")