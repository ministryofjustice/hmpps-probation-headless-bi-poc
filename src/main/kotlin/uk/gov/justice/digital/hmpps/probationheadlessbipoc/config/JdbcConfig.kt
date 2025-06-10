package uk.gov.justice.digital.hmpps.probationheadlessbipoc.config

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import uk.gov.justice.digital.hmpps.probationheadlessbipoc.secretsmanager.AWSSecretsManagerProvider
import uk.gov.justice.digital.hmpps.probationheadlessbipoc.secretsmanager.JdbcAuthDetails
import uk.gov.justice.digital.hmpps.probationheadlessbipoc.secretsmanager.SecretsManagerClient
import javax.sql.DataSource

@Configuration
@ConditionalOnProperty(name = ["spring.config.import"])
class JdbcConfig {

//  @Value("\${spring.config.import}")
  private val secretID: String = "arn:aws:secretsmanager:eu-west-2:771283872747:secret:dpr-redshift-secret-development-rLHcQZ"

  @Bean
  fun dataSource(): DataSource {
    val heartBeatEndpointDetails = jdbcAuthDetails
    val dataSource = DriverManagerDataSource()
    dataSource.url = "jdbc:redshift://" + heartBeatEndpointDetails.host + "/" + heartBeatEndpointDetails.dbName
    dataSource.username = heartBeatEndpointDetails.username
    dataSource.password = heartBeatEndpointDetails.password
    dataSource.setDriverClassName("com.amazon.redshift.jdbc.Driver")
    log.info("username from dataSource:{} ", dataSource.username)
    return dataSource
  }

  val jdbcAuthDetails: JdbcAuthDetails
    get() {
      val secretsManagerClient = SecretsManagerClient(AWSSecretsManagerProvider())
      return secretsManagerClient.getSecret(
        secretID!!,
        JdbcAuthDetails::class.java,
      )
    }

  @Bean
  fun namedParameterJdbcTemplate(dataSource: DataSource): NamedParameterJdbcTemplate = NamedParameterJdbcTemplate(dataSource)

  private companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
