package com.hmpp.data.probation.odatademo

import org.apache.olingo.commons.api.data.ContextURL
import org.apache.olingo.commons.api.data.Entity
import org.apache.olingo.commons.api.data.EntityCollection
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
import org.apache.olingo.server.api.processor.EntityCollectionProcessor
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions
import org.apache.olingo.server.api.uri.UriInfo
import org.apache.olingo.server.api.uri.UriResourceEntitySet
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.*

class BookEntityCollectionProcessor : EntityCollectionProcessor {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  private lateinit var odata: OData
  private lateinit var serviceMetadata: ServiceMetadata

  override fun init(odata: OData, serviceMetadata: ServiceMetadata) {
    this.odata = odata
    this.serviceMetadata = serviceMetadata
  }

  override fun readEntityCollection(
    request: ODataRequest,
    response: ODataResponse,
    uriInfo: UriInfo,
    responseFormat: ContentType,
  ) {
    val resourceParts = uriInfo.uriResourceParts
    if (resourceParts.isEmpty() || resourceParts[0] !is UriResourceEntitySet) {
      throw ODataApplicationException("Invalid URI format", 400, Locale.ENGLISH)
    }
    val resource = uriInfo.uriResourceParts[0] as UriResourceEntitySet
    val entitySet = resource.entitySet

    val books = listOf(
      BookDto(1, "Kotlin in Action", "Dmitry"),
      BookDto(2, "Effective Java", "Joshua"),
    )

    val entityCollection = EntityCollection()

    books.forEach {
      val entity = Entity()
      entity.addProperty(Property(null, "id", ValueType.PRIMITIVE, it.id))
      entity.addProperty(Property(null, "title", ValueType.PRIMITIVE, it.title))
      entity.addProperty(Property(null, "author", ValueType.PRIMITIVE, it.author))
      entity.id = URI("Books(${it.id})")
      entityCollection.entities.add(entity)
    }

    val serializer = odata.createSerializer(responseFormat)
    val edmEntityType: EdmEntityType = entitySet.entityType
    val contextUrl = ContextURL.with().entitySet(entitySet).build()
    val options = EntityCollectionSerializerOptions.with().contextURL(contextUrl).build()
    val serializedContent = serializer.entityCollection(
      serviceMetadata,
      edmEntityType,
      entityCollection,
      options,
    )

    response.content = serializedContent.content
    response.statusCode = 200
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString())
  }
}
