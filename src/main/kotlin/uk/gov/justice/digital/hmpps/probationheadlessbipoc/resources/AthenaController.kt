package uk.gov.justice.digital.hmpps.probationheadlessbipoc.resources

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.probationheadlessbipoc.data.AthenaQueryService
import uk.gov.justice.digital.hmpps.probationheadlessbipoc.data.StatementExecutionStatus

@RestController
@RequestMapping("/")
@ConditionalOnProperty(name = ["spring.config.import"])
class AthenaController(private val athenaQueryService: AthenaQueryService) {
  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  @GetMapping("athena/query")
  fun runQuery(): ResponseEntity<String> {
    val query = "select * from AwsDataCatalog.reports._83316aa4_c5f8_4504_a623_7a0ecde23b63"
    val database = "reports"
    val catalog = "AwsDataCatalog"
    return try {
      log.info("Starting query: $query")
      val result = athenaQueryService.startQueryExecution(query, database, catalog)
      ResponseEntity.ok().body(result)
    } catch (e: Exception) {
      ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Query failed: ${e.message}")
    }
  }

  @GetMapping("athena/query/status/{statementId}")
  fun getQueryExecutionStatus(
    @PathVariable("statementId") statementId: String,
  ): ResponseEntity<StatementExecutionStatus> = ResponseEntity
    .status(HttpStatus.OK)
    .body(
      athenaQueryService.getStatementStatus(statementId),
    )

  @GetMapping("redshift/query/result")
  fun getQueryExecutionResult(): ResponseEntity<List<Map<String, Any?>>> = ResponseEntity.ok().body(athenaQueryService.getQueryResults(null))
}
