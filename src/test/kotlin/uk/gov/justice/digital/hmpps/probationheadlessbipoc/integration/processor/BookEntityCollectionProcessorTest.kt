package uk.gov.justice.digital.hmpps.probationheadlessbipoc.integration.processor

import com.hmpp.data.probation.odatademo.BookEntityCollectionProcessor
import org.apache.olingo.commons.api.edm.EdmEntityType
import org.apache.olingo.commons.api.format.ContentType
import org.apache.olingo.commons.api.http.HttpHeader
import org.apache.olingo.server.api.OData
import org.apache.olingo.server.api.ODataRequest
import org.apache.olingo.server.api.ODataResponse
import org.apache.olingo.server.api.ServiceMetadata
import org.apache.olingo.server.api.serializer.ODataSerializer
import org.apache.olingo.server.api.serializer.SerializerResult
import org.apache.olingo.server.api.uri.UriInfo
import org.apache.olingo.server.api.uri.UriResourceEntitySet
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.ByteArrayInputStream

class BookEntityCollectionProcessorTest {

  private lateinit var bookEntityProcessor: BookEntityCollectionProcessor
  private val oData: OData = mock()
  private val serviceMetadata: ServiceMetadata = mock()
  private val response: ODataResponse = mock()
  private val request: ODataRequest = mock()
  private val uriInfo: UriInfo = mock()
  private val uriResourceEntitySet: UriResourceEntitySet = mock()
  private val serializer: ODataSerializer = mock()
  private val serializerResult: SerializerResult = mock()
  private val edmEntityType: EdmEntityType = mock()

  @BeforeEach
  fun setup() {
    bookEntityProcessor = BookEntityCollectionProcessor()
    bookEntityProcessor.init(oData, serviceMetadata)

    whenever(oData.createSerializer(ContentType.JSON)).thenReturn(serializer)
    whenever(serializer.entityCollection(any(), any(), any(), any())).thenReturn(serializerResult)
    whenever(serializerResult.content).thenReturn(ByteArrayInputStream("{}".toByteArray()))

    // Books URI
    whenever(uriInfo.uriResourceParts).thenReturn(listOf(uriResourceEntitySet))
    whenever(uriResourceEntitySet.entitySet).thenReturn(mock())
    whenever(uriResourceEntitySet.entitySet.entityType).thenReturn(edmEntityType)
  }

  @Test
  fun `should return JSON with HTTP 200 when reading books`() {
    bookEntityProcessor.readEntityCollection(request, response, uriInfo, ContentType.JSON)

    verify(response).setContent(any())
    verify(response).statusCode = 200
    verify(response).setHeader(HttpHeader.CONTENT_TYPE, ContentType.JSON.toContentTypeString())
  }
}
