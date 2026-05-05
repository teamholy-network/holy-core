import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Date

fun runGitCommand(cmd: String): String = try {
    Runtime.getRuntime().exec(cmd.split(" ").toTypedArray())
        .inputStream.bufferedReader().readText().trim()
} catch (e: Exception) {
    "unknown"
}

val buildConstantsDir = layout.buildDirectory.dir("generated/sources/buildConstants/main/java")

val generateBuildConstants by tasks.registering {
    outputs.dir(buildConstantsDir)
    outputs.upToDateWhen { false }

    doLast {
        val packageDir = buildConstantsDir.get().asFile.resolve("de/teamholy/core/api/constants")
        packageDir.mkdirs()

        val timeFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Date())
        val timeMillis = System.currentTimeMillis()
        val commit = runGitCommand("git rev-parse --verify --short HEAD")
        val commitCount = runGitCommand("git rev-list --count HEAD")
        val branch = runGitCommand("git rev-parse --abbrev-ref HEAD")
        val secOfDay = LocalTime.now().toSecondOfDay()
        val jdk = System.getProperty("java.version")
        val os = "${System.getProperty("os.name")}/${System.getProperty("os.arch")}"
        val gradleVer = gradle.gradleVersion
        val projectVer = version.toString()

        packageDir.resolve("Project.java").writeText(
            """
            package de.teamholy.core.api.constants;

            public class Project {

                public static final String BUILD_TIME_FORMAT = "$timeFormat";
                public static final String BUILD_TIME_MILLIS = "$timeMillis";
                public static final String BUILD_COMMIT = "$commit";
                public static final String BUILD_COMMIT_COUNT = "$commitCount";
                public static final String BUILD_BRANCH = "$branch";
                public static final String BUILD_SEC_OF_DAY = "$secOfDay";
                public static final String BUILD_JDK = "$jdk";
                public static final String BUILD_OS = "$os";
                public static final String BUILD_GRADLE = "$gradleVer";
                public static final String BUILD_VERSION = "$projectVer";
            }
            """.trimIndent()
        )
    }
}

sourceSets.main {
    java.srcDir(buildConstantsDir)
}

tasks.compileJava {
    dependsOn(generateBuildConstants)
}

dependencies {
    implementation(libs.en2do)
    implementation(libs.redisson)
    implementation(libs.jetbrains.annotations)
    implementation(libs.mongodb.bson)
    implementation(libs.rabbitmq.client)

    compileOnly(libs.cloudnet.bridge)
    compileOnly(libs.cloudnet.wrapper.jvm)
    implementation(libs.yamlbeans)
    compileOnly(libs.luckperms)
}
