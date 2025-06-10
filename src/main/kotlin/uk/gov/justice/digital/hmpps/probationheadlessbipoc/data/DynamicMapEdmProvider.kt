package uk.gov.justice.digital.hmpps.probationheadlessbipoc.data

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind
import org.apache.olingo.commons.api.edm.FullQualifiedName
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType
import org.apache.olingo.commons.api.edm.provider.CsdlProperty
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef
import org.apache.olingo.commons.api.edm.provider.CsdlSchema

class DynamicMapEdmProvider(
  private val entityName: String,
  private val data: List<Map<String, Any?>>,
  private val namespace: String = "probation.headless.bi.poc",
) : CsdlAbstractEdmProvider() {

  private val containerName = "Container"

  override fun getEntityType(entityTypeName: FullQualifiedName): CsdlEntityType? {
    if (entityTypeName.name != entityName) return null

    val sample = data.firstOrNull() ?: return null

    val properties = sample.map { (key, value) ->
      val edmType = resolveType(value)
      CsdlProperty().setName(key).setType(edmType)
    }

    return CsdlEntityType()
      .setName(entityName)
      .setProperties(properties)
      .setKey(listOf(CsdlPropertyRef().setName("offender_id")))
      .setOpenType(true)
  }

  override fun getEntitySet(entityContainer: FullQualifiedName?, entitySetName: String?): CsdlEntitySet? = if (entitySetName == entityName) {
    CsdlEntitySet().setName(entityName).setType(FullQualifiedName(namespace, entityName))
  } else {
    null
  }

  override fun getEntityContainer(): CsdlEntityContainer {
    val entitySet = getEntitySet(FullQualifiedName(namespace, containerName), entityName)
    return CsdlEntityContainer()
      .setName(containerName)
      .setEntitySets(listOfNotNull(entitySet))
  }

  override fun getEntityContainerInfo(entityContainerName: FullQualifiedName?): CsdlEntityContainerInfo = CsdlEntityContainerInfo().setContainerName(FullQualifiedName(namespace, this.containerName))

  override fun getSchemas(): List<CsdlSchema> {
    val entityType = getEntityType(FullQualifiedName(namespace, entityName)) ?: return emptyList()
    return listOf(
      CsdlSchema()
        .setNamespace(namespace)
        .setEntityTypes(listOf(entityType))
        .setEntityContainer(entityContainer),
    )
  }

  private fun resolveType(value: Any?): FullQualifiedName = when (value) {
    is Int -> EdmPrimitiveTypeKind.Int32.fullQualifiedName
    is Long -> EdmPrimitiveTypeKind.Int64.fullQualifiedName
    is Double -> EdmPrimitiveTypeKind.Double.fullQualifiedName
    is Float -> EdmPrimitiveTypeKind.Single.fullQualifiedName
    is Boolean -> EdmPrimitiveTypeKind.Boolean.fullQualifiedName
    is String -> EdmPrimitiveTypeKind.String.fullQualifiedName
    else -> EdmPrimitiveTypeKind.String.fullQualifiedName
  }
}
