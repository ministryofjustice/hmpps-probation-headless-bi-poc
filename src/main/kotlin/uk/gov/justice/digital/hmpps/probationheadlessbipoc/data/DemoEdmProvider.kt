package com.hmpp.data.probation.odatademo

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

class DemoEdmProvider : CsdlAbstractEdmProvider() {

  companion object {
    val NAMESPACE = "Odata.Demo"
    val ENTITYT_TYPE_NAME = "Book"
    val ENTITYT_SET_NAME = "Books"
    val FQN = FullQualifiedName(ENTITYT_SET_NAME, ENTITYT_TYPE_NAME)
  }

  override fun getEntityType(entityTypeName: FullQualifiedName?): CsdlEntityType? {
    if (entityTypeName == FQN) {
      val id = CsdlProperty().setName("id").setType(EdmPrimitiveTypeKind.Int32.fullQualifiedName)
      val title = CsdlProperty().setName("title").setType(EdmPrimitiveTypeKind.String.fullQualifiedName)
      val author = CsdlProperty().setName("author").setType(EdmPrimitiveTypeKind.String.fullQualifiedName)

      return CsdlEntityType().setName(ENTITYT_TYPE_NAME)
        .setProperties(listOf(id, title, author))
        .setKey(listOf(CsdlPropertyRef().setName("id")))
        .setOpenType(true)
    }
    return null
  }

  override fun getEntitySet(container: FullQualifiedName, entitySetName: String): CsdlEntitySet? = if (entitySetName == ENTITYT_SET_NAME) {
    CsdlEntitySet().setName(ENTITYT_SET_NAME).setType(FQN)
  } else {
    null
  }

  override fun getEntityContainer(): CsdlEntityContainer = CsdlEntityContainer().setName("Container")
    .setEntitySets(listOf(getEntitySet(FullQualifiedName(ENTITYT_SET_NAME, "Container"), ENTITYT_SET_NAME)))

  override fun getSchemas(): List<CsdlSchema> = listOf(
    CsdlSchema()
      .setNamespace(ENTITYT_SET_NAME)
      .setEntityTypes(listOf(getEntityType(FQN)))
      .setEntityContainer(entityContainer),
  )

  override fun getEntityContainerInfo(entityContainerName: FullQualifiedName?): CsdlEntityContainerInfo = CsdlEntityContainerInfo().setContainerName(FullQualifiedName(NAMESPACE, "Container"))
}
