package com.spprj.unq_dapps._s2_GrupoG.architecture

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaMethod
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

@AnalyzeClasses(
    packages = ["com.spprj.unq_dapps._s2_GrupoG"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class ArchitectureTest {

    @ArchTest
    val `controllers should follow naming convention` =
    classes()
    .that().areAnnotatedWith(RestController::class.java)
    .should().haveSimpleNameEndingWith("Controller")
    .because("Clean Code requires consistent naming to improve readability")


    @ArchTest
    val `controllers must not depend on repositories` =
    noClasses()
    .that().areAnnotatedWith(RestController::class.java)
    .should().dependOnClassesThat().resideInAPackage("..repositories..")
    .because("Controllers should delegate business logic to services, not repositories")

    @ArchTest
    val `public controller methods should return ResponseEntity` =
    methods()
    .that().arePublic()
    .and().areDeclaredInClassesThat().areAnnotatedWith(RestController::class.java)
    .should().haveRawReturnType(ResponseEntity::class.java)
    .because("Controllers must define clear HTTP responses using ResponseEntity")

    @ArchTest
    val `controllers should not be transactional` =
    noClasses()
    .that().areAnnotatedWith(RestController::class.java)
    .should().beAnnotatedWith(Transactional::class.java)
    .because("Transactional logic belongs in the service layer, not controllers")

    @ArchTest
    val `service implementations should follow naming convention` =
    classes()
    .that().resideInAPackage("..service..")
    .and().areAnnotatedWith(Service::class.java)
    .should().haveSimpleNameEndingWith("Impl")
    .because("Service implementation naming must be consistent to maintain clean architecture")

    @ArchTest
    val `repositories should follow naming convention` =
    classes()
    .that().resideInAPackage("..repositories..")
    .should().haveSimpleNameEndingWith("Repository")
    .because("Repository naming must remain recognizable and consistent")

}
