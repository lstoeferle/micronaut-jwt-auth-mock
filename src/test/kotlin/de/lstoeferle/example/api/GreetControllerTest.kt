package de.lstoeferle.example.api

import de.lstoeferle.example.security.TokenMock
import de.lstoeferle.example.security.UserRole
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MicronautTest

@MicronautTest(environments = [TokenMock.ENVIRONMENT])
class GreetControllerTest(
        tokenMock: TokenMock,
        @Client("/greet") client: RxHttpClient
) : StringSpec({

    "test without mock should return 403 status" {
        val exception = shouldThrow<HttpClientResponseException> {
            client.toBlocking().exchange<String>("/")
        }
        exception.status shouldBe HttpStatus.FORBIDDEN
    }

    "test with mock should return 200 status" {
        tokenMock.mock(name = "Tony Stark", roles = listOf(UserRole.GREETER)) {
            val response = client.toBlocking().exchange(HttpRequest.GET<Unit>("/"), String::class.java)
            response.status shouldBe HttpStatus.OK
            response.body() shouldBe "Hello Tony Stark"
        }
    }
})