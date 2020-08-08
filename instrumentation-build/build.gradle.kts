plugins {
    `java-library`
    id("com.github.johnrengelman.shadow")
}

java {
    // These classes are only used during the build. Since the build requires Java 8+,
    // these class files can also be Java 8.
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":newrelic-weaver"))
    testImplementation(project(":newrelic-weaver-api"))
    testImplementation("org.mockito:mockito-core:3.4.6")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
}
