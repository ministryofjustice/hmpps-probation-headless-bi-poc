package uk.gov.justice.digital.hmpps.probationheadlessbipoc.data

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import javax.sql.DataSource

abstract class RepositoryHelper {

  companion object {
    @JvmStatic
    protected val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @Autowired
  lateinit var context: ApplicationContext

  protected fun populateNamedParameterJdbcTemplate(dataSourceName: String? = null): NamedParameterJdbcTemplate {
    val dataSource = findDataSource(dataSourceName)
    return NamedParameterJdbcTemplate(dataSource)
  }

  protected fun populateJdbcTemplate(dataSourceName: String? = null): JdbcTemplate {
    val dataSource = findDataSource(dataSourceName)
    return JdbcTemplate(dataSource)
  }

  private fun findDataSource(dataSourceName: String?): DataSource {
    val dataSource = if (dataSourceName == null) {
      context.getBean(DataSource::class.java) as DataSource
    } else if (context.containsBean(dataSourceName)) {
      context.getBean(dataSourceName, DataSource::class) as DataSource
    } else {
      log.warn("No DataSource Bean found with name: {}", dataSourceName)
      context.getBean(DataSource::class.java) as DataSource
    }
    return dataSource
  }
}
