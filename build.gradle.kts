plugins {
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.5"

	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	id("org.sonarqube") version "5.1.0.4882"
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

repositories {
	mavenCentral()
}

dependencies {
    // --- Spring Boot ---
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // --- OpenAPI / Documentación ---
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // --- Utilidades ---
    implementation("org.jsoup:jsoup:1.18.1")

    // --- JWT ---
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // --- Selenium ---
    val seleniumVersion = "4.26.0"
    implementation(platform("org.seleniumhq.selenium:selenium-bom:$seleniumVersion"))
    implementation("org.seleniumhq.selenium:selenium-java")
    implementation("org.seleniumhq.selenium:selenium-chrome-driver")
    implementation("org.seleniumhq.selenium:selenium-support")

    // --- Database ---
    runtimeOnly("com.mysql:mysql-connector-j")

    // --- Testing ---
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

sonar {
	properties {
		property("sonar.projectKey", "JuliDeMaio_unq-dapps-2025s2-GrupoG")
		property("sonar.organization", "julidemaio")
		property("sonar.host.url", "https://sonarcloud.io")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
