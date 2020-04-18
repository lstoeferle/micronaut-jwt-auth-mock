package de.lstoeferle.example.security

import io.micronaut.security.authentication.*
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import javax.inject.Singleton

@Singleton
class AuthenticationProviderUsernamePassword : AuthenticationProvider {

    override fun authenticate(authenticationRequest: AuthenticationRequest<*, *>?): Publisher<AuthenticationResponse> {
        val identity: String = authenticationRequest?.identity as? String ?: ""
        val secret: String = authenticationRequest?.secret as? String ?: ""
        return if (identity == "user" && secret == "secret") {
            Flowable.just(UserDetails(identity, listOf(UserRole.GREETER)))
        } else {
            Flowable.just(AuthenticationFailed())
        }
    }
}

