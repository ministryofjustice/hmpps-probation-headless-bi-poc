package uk.gov.justice.digital.hmpps.probationheadlessbipoc.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import uk.gov.justice.hmpps.kotlin.auth.HmppsResourceServerConfiguration
import uk.gov.justice.hmpps.kotlin.auth.dsl.ResourceServerConfigurationCustomizer

@Configuration
class SecurityConfig {

  @Bean
  fun probationSecurityFilterChain(
    http: HttpSecurity,
    probationResourceServerCustomizer: ResourceServerConfigurationCustomizer,
  ): SecurityFilterChain = HmppsResourceServerConfiguration().hmppsSecurityFilterChain(http, probationResourceServerCustomizer)

  @Bean
  fun probationResourceServerCustomizer() = ResourceServerConfigurationCustomizer {
    unauthorizedRequestPaths { addPaths = setOf("/**") }
  }
}
