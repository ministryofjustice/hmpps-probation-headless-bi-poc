package uk.gov.justice.digital.hmpps.probationheadlessbipoc.data

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.athena.AthenaClient
import software.amazon.awssdk.services.athena.model.AthenaError
import software.amazon.awssdk.services.athena.model.GetQueryExecutionRequest
import software.amazon.awssdk.services.athena.model.QueryExecutionContext
import software.amazon.awssdk.services.athena.model.QueryExecutionStatus
import software.amazon.awssdk.services.athena.model.StartQueryExecutionRequest

const val QUERY_STARTED = "STARTED"
const val QUERY_FINISHED = "FINISHED"
const val QUERY_ABORTED = "ABORTED"
const val QUERY_FAILED = "FAILED"
const val QUERY_SUCCEEDED = "SUCCEEDED"
const val QUERY_CANCELLED = "CANCELLED"
const val QUERY_RUNNING = "RUNNING"
const val QUERY_QUEUED = "QUEUED"
const val QUERY_SUBMITTED = "SUBMITTED"

@Service
@ConditionalOnBean(AthenaClient::class)
@ConditionalOnProperty(name = ["spring.config.import"])
class AthenaQueryService(private val athenaClient: AthenaClient, private val athenaAndRedshiftCommonRepository: AthenaAndRedshiftCommonRepository) {

  fun startQueryExecution(query: String, database: String, catalog: String): String {
    val request = StartQueryExecutionRequest.builder()
      .queryString(query)
      .queryExecutionContext(
        QueryExecutionContext.builder()
          .database(database)
          .catalog(catalog)
          .build(),
      )
      .workGroup("dpr-generic-athena-workgroup")
      .build()
    val response = athenaClient.startQueryExecution(request)
    return response.queryExecutionId()
  }

  fun getStatementStatus(statementId: String): StatementExecutionStatus {
    val getQueryExecutionRequest = GetQueryExecutionRequest.builder()
      .queryExecutionId(statementId)
      .build()

    val getQueryExecutionResponse = athenaClient.getQueryExecution(getQueryExecutionRequest)
    val status = getQueryExecutionResponse.queryExecution().status()
    val stateChangeReason = status.stateChangeReason()
    val error: AthenaError? = status.athenaError()
    return StatementExecutionStatus(
      status = mapAthenaStateToRedshiftState(status.state().toString()),
      duration = calculateDuration(status),
      resultRows = 0,
      resultSize = 0,
      error = error?.errorMessage(),
      errorCategory = error?.errorCategory(),
      stateChangeReason = stateChangeReason,
    )
  }

  fun getQueryResults(tableId: String?): List<Map<String, Any?>> {
    val tableName = tableId ?: "_2e5dddcc_c1ff_4f3b_b73d_ad2fd3dac500"
    return athenaAndRedshiftCommonRepository.getExternalTableResult(tableName)
  }

  protected fun mapAthenaStateToRedshiftState(queryState: String): String {
    val athenaToRedshiftStateMappings = mapOf(
      QUERY_QUEUED to QUERY_SUBMITTED,
      QUERY_RUNNING to QUERY_STARTED,
      QUERY_SUCCEEDED to QUERY_FINISHED,
      QUERY_CANCELLED to QUERY_ABORTED,
    )
    return athenaToRedshiftStateMappings.getOrDefault(queryState, queryState)
  }

  private fun calculateDuration(status: QueryExecutionStatus): Long = status.completionDateTime()?.let { completion ->
    status.submissionDateTime()?.let { submission ->
      completion.minusMillis(submission.toEpochMilli()).toEpochMilli() * 1000000
    } ?: 0
  } ?: 0
}
