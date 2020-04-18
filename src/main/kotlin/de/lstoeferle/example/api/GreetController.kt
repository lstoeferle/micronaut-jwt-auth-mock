package de.lstoeferle.example.api

import de.lstoeferle.example.security.UserRole
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import java.security.Principal

@Controller("/greet")
@Secured(SecurityRule.IS_AUTHENTICATED)
class GreetController {

    @Get(value = "/", produces = [MediaType.TEXT_PLAIN])
    @Secured(UserRole.GREETER)
    fun greetWorld(principal: Principal) = "Hello ${principal.name}"
}