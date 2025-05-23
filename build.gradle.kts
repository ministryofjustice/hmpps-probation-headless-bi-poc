plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "8.1.0"
  kotlin("plugin.spring") version "2.1.20"
  id("jacoco")
  id("org.barfuin.gradle.jacocolog") version "3.1.0"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

val awsSdkVersion = "2.31.3"

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:1.4.3")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
  implementation("org.apache.olingo:odata-server-api:5.0.0")
  implementation("org.apache.olingo:odata-server-core:5.0.0")
  implementation("com.amazon.redshift:redshift-jdbc4-no-awssdk:1.2.45.1069")

  implementation("org.springframework.boot:spring-boot-starter-data-jpa")

//  implementation("uk.gov.justice.service.hmpps:hmpps-digital-prison-reporting-lib:7.10.3")
  implementation("software.amazon.awssdk:athena:$awsSdkVersion")

  implementation("com.h2database:h2")

  testImplementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter-test:1.4.3")
  testImplementation("org.wiremock:wiremock-standalone:3.13.0")
  testImplementation("io.swagger.parser.v3:swagger-parser:2.1.26") {
    exclude(group = "io.swagger.core.v3")
  }
}

kotlin {
  jvmToolchain(21)
}

repositories {
  mavenLocal()
  mavenCentral()
  maven("https://s3.amazonaws.com/redshift-maven-repository/release")
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
  }
}
