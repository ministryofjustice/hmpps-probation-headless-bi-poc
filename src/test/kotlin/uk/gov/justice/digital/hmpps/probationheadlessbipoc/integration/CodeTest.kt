package uk.gov.justice.digital.hmpps.probationheadlessbipoc.integration

import org.junit.jupiter.api.Test

class CodeTest {

  @Test
  fun `loop test`() {
    val data: List<Map<String, Any?>> = listOf(
      mapOf(
        "id" to 1,
        "title" to "Kotlin in Action",
        "author" to "Dmitry",
      ),
      mapOf(
        "id" to 2,
        "title" to "Effective Java",
        "author" to "Joshua",
      ),
    )

    for (entry in data) {
      println("User:")
      for ((key, value) in entry) {
        println(" $key: $value")
      }
    }

    val result = data.filter { entry -> entry.get("id") == 1 }

    println("User: $result ")
  }
}
