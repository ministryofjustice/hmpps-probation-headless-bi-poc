package uk.gov.justice.digital.hmpps.probationheadlessbipoc.secretsmanager

import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest
import com.fasterxml.jackson.databind.ObjectMapper

class SecretsManagerClient(provider: AWSSecretsManagerProvider) {
  private val secretsClient = provider.buildClient()

  fun <T> getSecret(secretId: String, type: Class<T>?): T {
    try {
      val request = GetSecretValueRequest().withSecretId(secretId)
      val secretValue = secretsClient.getSecretValue(request).secretString
      return ObjectMapper().readValue(secretValue, type)
    } catch (ex: Exception) {
      throw RuntimeException("Cannot retrieve secret $secretId", ex)
    }
  }
}
