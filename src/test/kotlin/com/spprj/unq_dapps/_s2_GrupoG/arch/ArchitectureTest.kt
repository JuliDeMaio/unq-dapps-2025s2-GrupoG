package com.spprj.unq_dapps._s2_GrupoG.architecture

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@AnalyzeClasses(
    packages = ["com.spprj.unq_dapps._s2_GrupoG"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class ArchitectureTest {

    @ArchTest
    val `controllers no deben depender de repositories` =
        noClasses()
            .that().areAnnotatedWith(RestController::class.java)
            .should().dependOnClassesThat().resideInAPackage("..repositories..")

    @ArchTest
    val `controllers deben devolver ResponseEntity` =
        methods()
            .that().areDeclaredInClassesThat().areAnnotatedWith(RestController::class.java)
            .and().arePublic()
            .should().haveRawReturnType(ResponseEntity::class.java)
}
