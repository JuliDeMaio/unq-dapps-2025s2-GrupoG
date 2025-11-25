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

// --- Versiones centralizadas ---
val seleniumVersion = "4.26.0"
val springdocVersion = "2.3.0"
val webdrivermanagerVersion = "5.9.2"
val jsoupVersion = "1.18.1"
val jjwtVersion = "0.11.5"
val mockitoKotlinVersion = "5.2.1"
val mockkVersion = "1.13.12"


dependencies {
    // --- Spring Boot ---
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")


    // --- Utilidades ---
    implementation("io.github.bonigarcia:webdrivermanager:$webdrivermanagerVersion")
    implementation("org.jsoup:jsoup:$jsoupVersion")

	// --- Métricas y monitoreo ---
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-prometheus")

    // --- JWT ---
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")

    // --- Selenium ---
    implementation(platform("org.seleniumhq.selenium:selenium-bom:$seleniumVersion"))
    implementation("org.seleniumhq.selenium:selenium-java")
    implementation("org.seleniumhq.selenium:selenium-chrome-driver")
    implementation("org.seleniumhq.selenium:selenium-support")

    // --- Runtime ---
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")
    runtimeOnly("com.mysql:mysql-connector-j")

    // --- Testing ---
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("com.h2database:h2")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation(kotlin("test"))
}

sonar {
	properties {
		property("sonar.projectKey", "JuliDeMaio_unq-dapps-2025s2-GrupoG")
		property("sonar.organization", "julidemaio")
		property("sonar.host.url", "https://sonarcloud.io")
		property("sonar.exclusions", "**/dto/**, **/config/**, **/model/**")
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
		xml.required.set(true)
		csv.required.set(false)
		html.required.set(true)
	}
}


