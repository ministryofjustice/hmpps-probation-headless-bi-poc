package uk.gov.justice.digital.hmpps.probationheadlessbipoc

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.probationheadlessbipoc.data.AthenaQueryService
import uk.gov.justice.digital.hmpps.probationheadlessbipoc.data.StatementExecutionStatus

@RestController
@RequestMapping("/athena")
class AthenaController(private val athenaQueryService: AthenaQueryService) {
  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  @GetMapping("/query")
  fun runQuery(): ResponseEntity<String> {
    val query = "select * from AwsDataCatalog.reports._136dedbf_33b2_4fa2_b938_ccb3f5010167"
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

  @GetMapping("/query/status/{statementId}/")
  fun getQueryExecutionStatus(
    @PathVariable("statementId") statementId: String,
  ): ResponseEntity<StatementExecutionStatus> = ResponseEntity
    .status(HttpStatus.OK)
    .body(
      athenaQueryService.getStatementStatus(statementId),
    )
}
