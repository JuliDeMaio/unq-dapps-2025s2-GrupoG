plugins {
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.5"
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	id("org.sonarqube") version "5.1.0.4882"
	jacoco
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

repositories {
	mavenCentral()
}

// Versiones centralizadas
val seleniumVersion = "4.26.0"

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

    // --- Selenium ---
    implementation(platform("org.seleniumhq.selenium:selenium-bom:$seleniumVersion"))
    implementation("org.seleniumhq.selenium:selenium-java")
    implementation("org.seleniumhq.selenium:selenium-chrome-driver")
    implementation("org.seleniumhq.selenium:selenium-support")

    // --- Runtime only ---
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
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

jacoco {
	toolVersion = "0.8.10"
}

tasks.test {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport) // corre jacoco después de los tests
}

tasks.jacocoTestReport {
	dependsOn(tasks.test) // primero tests, después el reporte
	reports {
		xml.required.set(true) // 👈 importante para sonar
		csv.required.set(false)
		html.required.set(true)
	}
}


