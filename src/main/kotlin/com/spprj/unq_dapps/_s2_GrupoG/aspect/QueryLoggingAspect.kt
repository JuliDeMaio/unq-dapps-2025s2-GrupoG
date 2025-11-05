package com.spprj.unq_dapps._s2_GrupoG.aspect

import com.spprj.unq_dapps._s2_GrupoG.service.UserService
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

@Aspect
@Component
class QueryLoggingAspect(
    private val userService: UserService
) {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    fun anyEndpoint() {
        // Intencionalmente vacío: define el pointcut sin lógica adicional
    }

    @AfterReturning(pointcut = "anyEndpoint()", returning = "result")
    fun logQuery(joinPoint: JoinPoint, result: Any?) {
        try {
            val methodSignature = joinPoint.signature
            val endpoint = "${methodSignature.declaringType.simpleName}.${methodSignature.name}"

            // Evitamos loguear el propio endpoint de consultas
            if (endpoint.contains("UserController.getUserQueries")) {
                return
            }

            val annotations = joinPoint.target::class.java.methods
                .firstOrNull { it.name == joinPoint.signature.name }
                ?.annotations
                ?.map { it.annotationClass.simpleName } ?: emptyList()

            val httpMethod = when {
                annotations.any { it?.contains("GetMapping") == true } -> "GET"
                annotations.any { it?.contains("PostMapping") == true } -> "POST"
                annotations.any { it?.contains("PutMapping") == true } -> "PUT"
                annotations.any { it?.contains("DeleteMapping") == true } -> "DELETE"
                else -> "UNKNOWN"
            }

            val userId = 1L
            val requestBody = joinPoint.args.firstOrNull()
            val responseBody = result

            userService.saveQueryLog(
                userId = userId,
                endpoint = endpoint,
                method = httpMethod,
                requestBody = requestBody,
                responseBody = responseBody
            )

            println("✅ Query logged automatically: $endpoint [$httpMethod]")
        } catch (e: Exception) {
            println("⚠️ Error logging query: ${e.message}")
        }
    }


}
