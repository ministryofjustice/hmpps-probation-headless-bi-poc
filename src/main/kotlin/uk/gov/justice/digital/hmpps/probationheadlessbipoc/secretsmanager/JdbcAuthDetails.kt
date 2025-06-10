package uk.gov.justice.digital.hmpps.probationheadlessbipoc.secretsmanager

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
class JdbcAuthDetails {
  @JsonProperty("username")
  var username: String? = null

  @JsonProperty("password")
  var password: String? = null

  @JsonProperty("host")
  var host: String? = null

  var dbName: String? = "datamart"

  @JsonProperty("port")
  var port: Int = 0

  constructor(username: String?, password: String?, host: String?, dbName: String?, port: Int) {
    this.username = username
    this.password = password
    this.host = host
    this.dbName = dbName
    this.port = port
  }

  constructor()

  override fun equals(other: Any?): Boolean {
    if (other == null || javaClass != other.javaClass) return false
    val that = other as JdbcAuthDetails
    return port == that.port && username == that.username && password == that.password && host == that.host && dbName == that.dbName
  }

  override fun hashCode(): Int = Objects.hash(username, password, host, dbName, port)
}
