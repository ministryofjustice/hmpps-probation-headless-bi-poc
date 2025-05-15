package com.hmpp.data.probation.odatademo

import org.apache.olingo.commons.api.data.ContextURL
import org.apache.olingo.commons.api.data.Entity
import org.apache.olingo.commons.api.data.Property
import org.apache.olingo.commons.api.data.ValueType
import org.apache.olingo.commons.api.edm.EdmEntityType
import org.apache.olingo.commons.api.format.ContentType
import org.apache.olingo.commons.api.http.HttpHeader
import org.apache.olingo.server.api.OData
import org.apache.olingo.server.api.ODataApplicationException
import org.apache.olingo.server.api.ODataRequest
import org.apache.olingo.server.api.ODataResponse
import org.apache.olingo.server.api.ServiceMetadata
import org.apache.olingo.server.api.processor.EntityProcessor
import org.apache.olingo.server.api.serializer.EntitySerializerOptions
import org.apache.olingo.server.api.uri.UriInfo
import org.apache.olingo.server.api.uri.UriResourceEntitySet
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.*

class BookEntityProcessor : EntityProcessor {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  private lateinit var odata: OData
  private lateinit var serviceMetadata: ServiceMetadata

  override fun init(odata: OData, serviceMetadata: ServiceMetadata) {
    this.odata = odata
    this.serviceMetadata = serviceMetadata
  }

  override fun readEntity(
    request: ODataRequest,
    response: ODataResponse,
    uriInfo: UriInfo,
    responseFormat: ContentType,
  ) {
    val resource = uriInfo.uriResourceParts[0] as UriResourceEntitySet
    val entitySet = resource.entitySet
    val keyPredicate = resource.keyPredicates[0]
    val key = keyPredicate.text.replace("'", "").toInt()

    log.info("keyPredicate: {}", key)

    val book = listOf(
      BookDto(1, "Kotlin in Action", "Dmitry"),
      BookDto(2, "Effective Java", "Joshua"),
    ).find { it.id == key } ?: throw ODataApplicationException(
      "Book not found",
      404,
      Locale.ENGLISH,
    )

    val entity = Entity()
    entity.addProperty(Property(null, "id", ValueType.PRIMITIVE, book.id))
    entity.addProperty(Property(null, "title", ValueType.PRIMITIVE, book.title))
    entity.addProperty(Property(null, "author", ValueType.PRIMITIVE, book.author))
    entity.id = URI("Books(${book.id})")

    val serializer = odata.createSerializer(responseFormat)
    val edmEntityType: EdmEntityType = entitySet.entityType
    val contextUrl = ContextURL.with().entitySet(entitySet).build()
    val options = EntitySerializerOptions.with().contextURL(contextUrl).build()

    val result = serializer.entity(
      serviceMetadata,
      edmEntityType,
      entity,
      options,
    )
    response.content = result.content
    response.statusCode = response.statusCode
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString())
  }

  override fun createEntity(p0: ODataRequest?, p1: ODataResponse?, p2: UriInfo?, p3: ContentType?, p4: ContentType?) {
    TODO("Not yet implemented")
  }

  override fun updateEntity(p0: ODataRequest?, p1: ODataResponse?, p2: UriInfo?, p3: ContentType?, p4: ContentType?) {
    TODO("Not yet implemented")
  }

  override fun deleteEntity(p0: ODataRequest?, p1: ODataResponse?, p2: UriInfo?) {
    TODO("Not yet implemented")
  }
}
