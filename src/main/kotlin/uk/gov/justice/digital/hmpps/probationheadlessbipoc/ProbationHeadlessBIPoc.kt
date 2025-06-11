package uk.gov.justice.digital.hmpps.probationheadlessbipoc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan

@ServletComponentScan
@SpringBootApplication(
  exclude = [
    DataSourceAutoConfiguration::class,
    DataSourceTransactionManagerAutoConfiguration::class,
  ],
)
class ProbationHeadlessBIPoc

fun main(args: Array<String>) {
  runApplication<ProbationHeadlessBIPoc>(*args)
}
