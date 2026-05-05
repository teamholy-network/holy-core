plugins {
    alias(libs.plugins.gradleup.shadow) apply false
}

val holyRepoUser: String? = findProperty("HolyRepoUser") as String?
val holyRepoPass: String? = findProperty("HolyRepoPass") as String?

// Capture catalog references at root scope — `libs` is not accessible inside subprojects {}
val shadowPluginId = libs.plugins.gradleup.shadow.get().pluginId
val lombokDep = libs.lombok

subprojects {
    apply(plugin = "java")
    apply(plugin = shadowPluginId)
    apply(plugin = "maven-publish")

    group = rootProject.property("projectGroup") as String
    version = rootProject.property("projectVersion") as String

    repositories {
        mavenLocal()
        mavenCentral()

        maven {
            name = "cloudnet-releases"
            url = uri("https://repo.cloudnetservice.eu/releases/")
            metadataSources {
                gradleMetadata()
                mavenPom()
                artifact()
            }
            content {
                includeGroup("de.dytanic.cloudnet")
                includeGroup("eu.cloudnetservice")
            }
        }
        maven {
            name = "mvnrepository"
            url = uri("https://mvn.lumine.io/repository/maven-public/")
            content {
                includeGroup("org.spigotmc")
                includeGroup("com.comphenix")
            }
        }
        maven {
            name = "carmrepository"
            url = uri("https://repo.carm.cc/repository/maven-public/")
            content {
                includeGroup("com.comphenix.protocol")
                includeGroup("com.mojang")
            }
        }
        maven {
            name = "spigot-repo"
            url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
            content {
                includeGroup("org.spigotmc")
            }
        }
        maven {
            name = "paper-repo"
            url = uri("https://papermc.io/repo/repository/maven-public/")
            content {
                includeGroup("io.papermc.paper")
                includeGroup("com.destroystokyo.paper")
            }
        }
        maven {
            name = "dmulloy2-repo"
            url = uri("https://repo.dmulloy2.net/repository/public/")
            content {
                includeGroup("com.comphenix")
            }
        }
        maven {
            name = "jitpack-repo"
            url = uri("https://jitpack.io/")
            content {
                includeGroupByRegex("com\\.github\\..+")
            }
        }
        maven {
            name = "codemc-repo"
            url = uri("https://repo.codemc.io/repository/maven-public/")
            content {
                includeGroup("com.gmail.filoghost.holographicdisplays")
            }
        }
        maven {
            name = "glaremaster-repo"
            url = uri("https://repo.glaremasters.me/repository/public/")
            content {
                includeGroupByRegex("me\\.glaremasters(\\..+)?")
            }
        }
        maven {
            name = "sonatype-repo"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
            content {
                includeGroupByRegex(".*")
            }
        }
    }

    tasks.named<org.gradle.api.tasks.bundling.AbstractArchiveTask>("shadowJar") {
        doLast {
            val outputDir = project.rootProject.file("output")
            project.copy {
                from(archiveFile)
                into(outputDir)
            }
            println("Nya~ Copied shiny plugin ${archiveFile.get().asFile.name} to output folder! UwU")
        }
    }

    dependencies {
        "compileOnly"(lombokDep)
        "annotationProcessor"(lombokDep)
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    if (name !in listOf("bungee-login", "holy-core-cloudmodule")) {
        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("normalJar") {
                    from(components["java"])
                    groupId = project.group.toString()
                    artifactId = project.name
                    version = project.version.toString()
                }
            }
            repositories {
                maven {
                    url = uri("https://repo.gin1.cc/repository/releases/")
                    credentials {
                        username = holyRepoUser
                        password = holyRepoPass
                    }
                }
            }
        }

        tasks.named("publish") {
            dependsOn("jar")
        }
    }
}