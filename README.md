# hmpps-probation-headless-bi-poc

[![repo standards badge](https://img.shields.io/badge/endpoint.svg?&style=flat&logo=github&url=https%3A%2F%2Foperations-engineering-reports.cloud-platform.service.justice.gov.uk%2Fapi%2Fv1%2Fcompliant_public_repositories%2Fhmpps-template-kotlin)](https://operations-engineering-reports.cloud-platform.service.justice.gov.uk/public-report/hmpps-template-kotlin "Link to report")
[![Docker Repository on ghcr](https://img.shields.io/badge/ghcr.io-repository-2496ED.svg?logo=docker)](https://ghcr.io/ministryofjustice/hmpps-probation-headless-bi-poc)
[![API docs](https://img.shields.io/badge/API_docs_-view-85EA2D.svg?logo=swagger)](https://hmpps-template-kotlin-dev.hmpps.service.justice.gov.uk/webjars/swagger-ui/index.html?configUrl=/v3/api-docs)

This project is generated from ministryofjustice/hmpps-template-kotlin

Requires Java 17 or above

#### CODEOWNER

Team : hmpps-digital-prison-reporting

Email : digitalprisonreporting@digital.justice.gov.uk

## Overview

The headless BI application provides a OData api endpoint that allows external systems such as Power BI in this case to retrieve reports data programmatically.
This endpoint serves as a OData API interface, enabling Power BI client to access data products as reports in a structured and standardized format using OData protocal. 
The main function of this endpoint is to expose BI reports in response to HTTP requests. The application processes theses OData queries, fetches the appropriate report data 
from the backend, and returns the results in a machine-readable format, typically JSON.

For the purpose of a Proof of Concept (POC), 
- Authentication has been intentionally disabled on this endpoint. This was done to simplify testing and accelerating development by allowing developers to access the reports to 
test the endpoint in Power BI client. However , it is important to note that this configuration is only suitable for internal environments. Authentication should be re-enabled before deploying the application to any production.

- Reporting lib has not been integrated. at the moment the data product dataset can be accessed through redshift repository from EC2 instance arns access.   
            
## Architecture

Please follow the link for the architectural design and deployment setup: https://dsdmoj.atlassian.net/wiki/spaces/DPR/pages/5751472208/Headless+BI+Probation+PoC

## Local Development

This project uses gradle which is bundled with the repository and also makes use
of

- [Spring Boot](https://spring.io/projects/spring-boot) - for compile time dependency injection
- [lombok](https://projectlombok.org/) - to reduce boilerplate when creating data classes
- [jacoco](https://docs.gradle.org/current/userguide/jacoco_plugin.html) - for test coverage reports

## Testing

> **Note** - test coverage reports are enabled by default and after running the
> tests the report will be written to build/reports/jacoco/test/html

### Unit Tests

The unit tests use JUnit5 and Mockito where appropriate. Use the following to
run the tests.

```
    ./gradlew clean test
```

## Contributing

Please adhere to the following guidelines when making contributions to the
project.

### Documentation

- Keep all code commentary and documentation up to date

### Branch Naming

- Use a JIRA ticket number where available
- Otherwise a short descriptive name is acceptable

### Commit Messages

- Prefix any commit messages with the JIRA ticket number where available
- Otherwise use the prefix `NOJIRA`

### Pull Requests

- Reference or link any relevant JIRA tickets in the pull request notes
- At least one approval is required before a PR can be merged

## Running the application locally

The application comes with a default spring profile that includes default settings for running locally. This is not
necessary when deploying to kubernetes as these values are included in the helm configuration templates -
e.g. `application.yml`.

There is also a `docker-compose.yml` that can be used to run a local instance of the template in docker and also an
instance of HMPPS Auth (required if your service calls out to other services using a token).

HMPPS Auth is disabled for the POC for now. In future we will add it. 

```bash
docker compose pull && docker compose up
```

will build the application and run it and HMPPS Auth within a local docker instance.

### Running the application in Intellij

```bash
docker compose pull && docker compose up --scale hmpps-probation-headless-bi-poc=0
```

will just start a docker instance of HMPPS Auth. The application should then be started with a default active profile
in Intellij.
