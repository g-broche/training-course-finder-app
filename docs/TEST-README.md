# Finder Backend Tests

This document gives a high-level overview of the project test strategy.

## Test Scope

Test sources are located under `src/test/java/com/example/finder`.

The suite is split by concern:

- Controller integration tests for public endpoints.
- Controller integration tests for admin endpoints.
- Utility-focused unit tests (JWT, validators, string/image/sanitizer helpers).
- Application context smoke test (`FinderApplicationTests`).

## Run Tests

From the backend root:

```bash
mvn test
```

## Reports

Surefire reports are generated in `target/surefire-reports`.
