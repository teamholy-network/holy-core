dependencies {
    implementation(libs.en2do)
    implementation(libs.redisson)
    implementation(libs.jedis)
    implementation(libs.jetbrains.annotations)
    implementation(libs.rabbitmq.client)
    compileOnly(libs.cloudnet.bridge)
    compileOnly(libs.cloudnet.wrapper.jvm)
    compileOnly(libs.bungeecord.api)
    compileOnly(libs.luckperms)
    implementation(libs.trove4j)
    implementation(project(":holy-core-api"))
}