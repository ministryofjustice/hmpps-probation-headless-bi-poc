package uk.gov.justice.digital.hmpps.probationheadlessbipoc.secretsmanager

import com.amazonaws.services.secretsmanager.AWSSecretsManager
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder

class AWSSecretsManagerProvider {

  fun buildClient(): AWSSecretsManager = AWSSecretsManagerClientBuilder.standard().build()
}
