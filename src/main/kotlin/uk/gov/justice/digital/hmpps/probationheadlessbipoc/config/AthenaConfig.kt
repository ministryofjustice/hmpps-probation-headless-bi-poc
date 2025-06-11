package uk.gov.justice.digital.hmpps.probationheadlessbipoc.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.athena.AthenaClient

@Configuration
class AthenaConfig {

  @Bean
  fun athenaClient(): AthenaClient = AthenaClient.builder()
    .region(Region.of("eu-west-2"))
    .build()
}
