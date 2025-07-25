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
import org.springframework.stereotype.Component
import java.net.URI
import java.util.*

@Component
class ReportsEntityCollectionProcessor(
  private val entityName: String,
  private val data: List<Map<String, Any?>>,
) : EntityCollectionProcessor {

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
    log.info("entityName: {}", entityName)
    val resourceParts = uriInfo.uriResourceParts
    if (resourceParts.isEmpty() || resourceParts[0] !is UriResourceEntitySet) {
      throw ODataApplicationException("Invalid URI format", 400, Locale.ENGLISH)
    }
    val resource = uriInfo.uriResourceParts[0] as UriResourceEntitySet
    val entitySet = resource.entitySet

    val entityCollection = EntityCollection()
    for (entry in data) {
      println("User:")
      val entity = Entity()
      for ((key, value) in entry) {
        println(" $key: $value")
        entity.addProperty(Property(null, key, ValueType.PRIMITIVE, value))
      }
      entity.id = URI("Reports(${entry["offender_id"]})")
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
