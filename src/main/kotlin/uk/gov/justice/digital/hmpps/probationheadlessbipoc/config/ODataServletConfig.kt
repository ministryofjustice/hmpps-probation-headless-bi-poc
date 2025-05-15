package uk.gov.justice.digital.hmpps.probationheadlessbipoc.config

import com.hmpp.data.probation.odatademo.BookEntityCollectionProcessor
import com.hmpp.data.probation.odatademo.BookEntityProcessor
import com.hmpp.data.probation.odatademo.DemoEdmProvider
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider
import org.apache.olingo.server.api.OData
import org.apache.olingo.server.api.ODataHttpHandler
import org.apache.olingo.server.api.ServiceMetadata
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException

@Configuration
class ODataServletConfig {

  @Bean
  fun odataServlet(): ServletRegistrationBean<HttpServlet> {
    val servlet = object : HttpServlet() {
      @Throws(ServletException::class, IOException::class)
      override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val odata: OData = OData.newInstance()
        val edm: CsdlEdmProvider = DemoEdmProvider()
        val metadata: ServiceMetadata = odata.createServiceMetadata(edm, listOf())
        val handler: ODataHttpHandler = odata.createHandler(metadata)

        handler.register(BookEntityCollectionProcessor())
        handler.register(BookEntityProcessor())
        handler.process(req, resp)
      }
    }
    return ServletRegistrationBean(servlet, "/odata/*")
  }
}
