package uk.gov.justice.digital.hmpps.probationheadlessbipoc.data

import org.apache.commons.lang3.time.StopWatch
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
@Primary
@ConditionalOnProperty(name = ["spring.config.import"])
class AthenaAndRedshiftCommonRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  fun getExternalTableResult(
    tableId: String,
  ): List<Map<String, Any?>> {
    val stopwatch = StopWatch.createStarted()
    val query = "SELECT * FROM reports.$tableId limit 10"
    log.info("Query to get results: {}", query)
    val result = jdbcTemplate
      .queryForList(
        query,
        MapSqlParameterSource(),
      )
      .map {
        transformTimestampToLocalDateTime(it)
      }
    stopwatch.stop()
    log.info("Query Execution time in ms: {}", stopwatch)
    return result
  }

  private fun transformTimestampToLocalDateTime(it: MutableMap<String, Any>) = it.entries.associate { (k, v) ->
    if (v is Timestamp) {
      k to v.toLocalDateTime()
    } else {
      k to v
    }
  }
}
