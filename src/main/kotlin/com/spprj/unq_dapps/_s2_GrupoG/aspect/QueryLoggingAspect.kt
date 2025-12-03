package com.spprj.unq_dapps._s2_GrupoG.aspect

import com.spprj.unq_dapps._s2_GrupoG.service.impl.UserServiceImpl
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Aspect
@Component
class QueryLoggingAspect(
    private val userService: UserServiceImpl)
 {

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

             // 🔥 NUEVO (lo mínimo indispensable)
             val method = joinPoint.target::class.java.methods
                 .firstOrNull { it.name == methodSignature.name }

             val params = method?.parameters ?: emptyArray()
             val args = joinPoint.args

             val pathParams = mutableMapOf<String, Any?>()
             val queryParams = mutableMapOf<String, Any?>()
             var requestBody: Any? = emptyMap<String, Any>()

             for (i in params.indices) {
                 val param = params[i]
                 val value = args[i]

                 when {
                     param.isAnnotationPresent(PathVariable::class.java) ->
                         pathParams[param.name ?: "param$i"] = value

                     param.isAnnotationPresent(RequestParam::class.java) ->
                         queryParams[param.name ?: "param$i"] = value

                     param.isAnnotationPresent(RequestBody::class.java) ->
                         requestBody = value
                 }
             }

             // 🔥 Mantengo tu saveQueryLog
             userService.saveQueryLog(
                 userId = 1L,
                 endpoint = endpoint,
                 method = httpMethod,
                 requestBody = requestBody,
                 responseBody = result,
                 pathParams = pathParams,
                 queryParams = queryParams
             )

             println("✅ Query logged automatically: $endpoint [$httpMethod]")
         } catch (e: Exception) {
             println("⚠️ Error logging query: ${e.message}")
         }
     }



 }
