package uk.gov.justice.digital.hmpps.probationheadlessbipoc.resources

import com.hmpp.data.probation.odatademo.ReportsEntityCollectionProcessor
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.olingo.server.api.OData
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import uk.gov.justice.digital.hmpps.probationheadlessbipoc.data.AthenaQueryService
import uk.gov.justice.digital.hmpps.probationheadlessbipoc.data.DynamicMapEdmProvider

@WebServlet(urlPatterns = ["/probation/*"], loadOnStartup = 1)
@ConditionalOnProperty(name = ["spring.config.import"])
class ProbationServlet(
  private val athenaQueryService: AthenaQueryService,
) : HttpServlet() {

  override fun service(req: HttpServletRequest, resp: HttpServletResponse) {
    val tableId = req.getParameter("tableId")

    if (!tableId.isNullOrBlank()) {
      log("Reports tableName: $tableId")
    }

    val queryResults = athenaQueryService.getQueryResults(tableId)

    val edmProvider = DynamicMapEdmProvider("Reports", queryResults)
    val processors = ReportsEntityCollectionProcessor("Reports", queryResults)
    val processor = ReportsEntityCollectionProcessor("Reports", queryResults)

    val odata = OData.newInstance()
    val serviceMetadata = odata.createServiceMetadata(edmProvider, listOf())

    val handler = odata.createHandler(serviceMetadata)

    handler.register(processors)
    handler.register(processor)
    handler.process(req, resp)
  }
}
