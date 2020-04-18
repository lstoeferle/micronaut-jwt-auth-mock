package de.lstoeferle.example.security

import de.lstoeferle.example.security.TokenMock.Companion.ENVIRONMENT
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requires
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.authentication.DefaultAuthentication
import io.micronaut.security.token.TokenAuthenticationFetcher
import io.micronaut.security.token.reader.TokenResolver
import io.micronaut.security.token.validator.TokenValidator
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import javax.inject.Singleton

@Requires(env = [ENVIRONMENT])
@Replaces(TokenAuthenticationFetcher::class)
@Singleton
class TokenMock(
        tokenValidators: MutableCollection<TokenValidator>?,
        tokenResolver: TokenResolver?,
        eventPublisher: ApplicationEventPublisher?
) : TokenAuthenticationFetcher(tokenValidators, tokenResolver, eventPublisher) {

    companion object {
        // Environment for tests with mocked token
        const val ENVIRONMENT = "TEST_WITH_MOCK_TOKEN"

        private const val CLAIM_USER = "name"
        private const val CLAIM_ROLES = "roles"
    }

    private var claims: HashMap<String, Any> = hashMapOf()

    /**
     * Saves the token claims in a [HashMap] for the following token mock.
     */
    fun beforeMock(name: String, roles: Collection<String>, claims: HashMap<String, Any>) = this.claims.apply {
        clear()
        putAll(claims)
        put(CLAIM_USER, name)
        put(CLAIM_ROLES, roles)
    }
    
    /**
     * This inline function wraps another code block by mocking the JWT auth with
     * the specified user information.
     *
     * @param name Name of the user
     * @param roles Roles mapped to the user
     * @param claims Custom JWT claims (if required)
     * @param block Function block to be executed with mock token
     */
    inline fun mock(
            name: String = "user",
            roles: Collection<String> = emptyList(),
            claims: HashMap<String, Any> = hashMapOf(),
            block: () -> Unit
    ) {
        beforeMock(name, roles, claims)
        block()
    }

    /**
     * This function usually is called to check the JWT token. We simply return the [DefaultAuthentication]
     * object with the specified name, roles and custom claims.
     */
    override fun fetchAuthentication(request: HttpRequest<*>?): Publisher<Authentication> {
        val username = claims[CLAIM_USER] as? String ?: ""
        return Flowable.just(DefaultAuthentication(username, claims))
    }
}