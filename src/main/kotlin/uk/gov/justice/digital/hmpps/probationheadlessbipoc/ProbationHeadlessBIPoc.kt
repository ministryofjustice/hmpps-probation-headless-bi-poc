package uk.gov.justice.digital.hmpps.probationheadlessbipoc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan

@ServletComponentScan
@SpringBootApplication
class ProbationHeadlessBIPoc

fun main(args: Array<String>) {
  runApplication<ProbationHeadlessBIPoc>(*args)
}
