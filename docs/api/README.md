# Finder Backend API

This document provides a quick API map and access information for OpenAPI documentation.

## API Overview

The backend API is organized under the `/api` prefix with public and admin route groups.

Main route groups:

- `/api/auth`, `/api/users`
- `/api/announces`, `/api/categories`, `/api/discussions`
- `/api/admin/auth`, `/api/admin/users`, `/api/admin/announces`, `/api/admin/discussions`

## Swagger / OpenAPI

The project includes OpenAPI documentation with Springdoc (`springdoc-openapi-starter-webmvc-ui`).

After starting the app locally, use:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Alternative Swagger path: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Swagger endpoints are explicitly allowed in security configuration.

## Notes

- Authentication-protected routes require a valid user or admin session/token depending on endpoint.
- Multipart endpoints are used for announce creation with image upload.
- Pagination is used on list endpoints where relevant.
