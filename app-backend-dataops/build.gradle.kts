plugins {
    kotlin("jvm")
}

kotlin {
    sourceSets.all {
        languageSettings {
            languageVersion = "2.0"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jooq:jooq-codegen:3.19.5")
    implementation("org.flywaydb:flyway-database-postgresql:10.8.1")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("ch.qos.logback:logback-classic:1.5.2")
}
