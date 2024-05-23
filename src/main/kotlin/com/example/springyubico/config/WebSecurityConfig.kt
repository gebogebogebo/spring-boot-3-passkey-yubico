package com.example.springyubico.config

import com.example.springyubico.Fido2AuthenticationFilter
import com.example.springyubico.Fido2AuthenticationProvider
import com.example.springyubico.PasswordAuthenticationFilter
import com.example.springyubico.PasswordAuthenticationProvider
import com.example.springyubico.UsernameAuthenticationFilter
import com.example.springyubico.UsernameAuthenticationProvider
import com.example.springyubico.UsernameAuthenticationSuccessHandler
import com.example.springyubico.service.SampleUserDetailsService
import com.example.springyubico.util.SecurityContextUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.context.DelegatingSecurityContextRepository
import org.springframework.security.web.context.HttpSessionSecurityContextRepository


@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    @Autowired private val usernameAuthenticationProvider: UsernameAuthenticationProvider,
    @Autowired private val passwordAuthenticationProvider: PasswordAuthenticationProvider,
    @Autowired private val fido2AuthenticationProvider: Fido2AuthenticationProvider,
    @Autowired private val userDetailsService: SampleUserDetailsService
) {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        authenticationManager: AuthenticationManager,
    ): SecurityFilterChain {
//        authenticationManager(http)

        http
            .authorizeHttpRequests { authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                    .requestMatchers("/login", "/login-fido2", "/authenticate/option").permitAll()
                    .requestMatchers("/password").hasAnyAuthority(SecurityContextUtil.Auth.AUTHENTICATED_USERNAME.value)
                    .requestMatchers("/**").hasRole(SecurityContextUtil.Role.USER.name)
            }
            .formLogin { formLogin ->
                formLogin
                    .loginPage("/login").permitAll()
//                    .successHandler(UsernameAuthenticationSuccessHandler("/password", "/mypage"))
//                    .failureUrl("/login?error")
            }
            .addFilterAt(createUsernameAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter::class.java)
            .addFilterAt(createPasswordAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter::class.java)
            .addFilterAt(createFido2AuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter::class.java)
            .csrf { csrf ->
                csrf.ignoringRequestMatchers(
                    "/authenticate/option",
                    "/register/option",
                    "/register/verify"
                )
            }
            .headers { headers ->
                headers.frameOptions { it.disable() }
            }
            .authenticationManager(authenticationManager)

        return http.build()
    }

//    @Bean
//    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
//        return authenticationConfiguration.authenticationManager
//    }

    @Bean
    fun authenticationManager(
        http: HttpSecurity
    ): AuthenticationManager {
        return createAuthenticationManagerBuilder(http).build()
    }

    private fun createAuthenticationManagerBuilder(http: HttpSecurity): AuthenticationManagerBuilder {
        val auth = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        configure(auth)
        return auth
    }

    private fun configure(auth: AuthenticationManagerBuilder) {
        // setUserDetailsService
        usernameAuthenticationProvider.setUserDetailsService(userDetailsService)
        passwordAuthenticationProvider.setUserDetailsService(userDetailsService)

        // authenticationProvider
        auth.authenticationProvider(usernameAuthenticationProvider)
        auth.authenticationProvider(passwordAuthenticationProvider)
        auth.authenticationProvider(fido2AuthenticationProvider)
    }

    @Bean
    fun authenticationFilter(
        authenticationManager: AuthenticationManager
    ): UsernamePasswordAuthenticationFilter {
        return createPasswordAuthenticationFilter(authenticationManager)
    }

//    @Bean
//    fun authenticationFilter(
//        authenticationManager: AuthenticationManager
//    ): PasswordAuthenticationFilter {
//        return createPasswordAuthenticationFilter(authenticationManager)
//    }
//
//    @Bean
//    fun authenticationFilter2(
//        authenticationManager: AuthenticationManager
//    ): Fido2AuthenticationFilter {
//        return createFido2AuthenticationFilter(authenticationManager)
//    }

    private fun createUsernameAuthenticationFilter(authenticationManager: AuthenticationManager): UsernamePasswordAuthenticationFilter {
        return UsernameAuthenticationFilter("/login", "POST").apply {
            setSecurityContextRepository(
                DelegatingSecurityContextRepository(
//                    RequestAttributeSecurityContextRepository(),
                    HttpSessionSecurityContextRepository()
                )
            )
            setAuthenticationManager(authenticationManager)
            setAuthenticationSuccessHandler(UsernameAuthenticationSuccessHandler("/password", "/mypage"))
            setAuthenticationFailureHandler(SimpleUrlAuthenticationFailureHandler("/login?error"))
        }
    }

    private fun createPasswordAuthenticationFilter(authenticationManager: AuthenticationManager): PasswordAuthenticationFilter {
        return PasswordAuthenticationFilter("/password", "POST").apply {
            setSecurityContextRepository(
                DelegatingSecurityContextRepository(
//                    RequestAttributeSecurityContextRepository(),
                    HttpSessionSecurityContextRepository()
                )
            )
            setAuthenticationManager(authenticationManager)
            setAuthenticationSuccessHandler(SimpleUrlAuthenticationSuccessHandler("/mypage"))
            setAuthenticationFailureHandler(SimpleUrlAuthenticationFailureHandler("/login?error"))
        }
    }

    private fun createFido2AuthenticationFilter(authenticationManager: AuthenticationManager): Fido2AuthenticationFilter {
        return Fido2AuthenticationFilter("/login-fido2", "POST").apply {
            setSecurityContextRepository(
                DelegatingSecurityContextRepository(
//                    RequestAttributeSecurityContextRepository(),
                    HttpSessionSecurityContextRepository()
                )
            )

            setAuthenticationManager(authenticationManager)
            setAuthenticationSuccessHandler(SimpleUrlAuthenticationSuccessHandler("/mypage"))
            setAuthenticationFailureHandler(SimpleUrlAuthenticationFailureHandler("/login?error"))
        }
    }

}
