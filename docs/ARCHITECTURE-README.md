# Finder Backend Architecture

This document describes the backend architecture and project layout.

## Architecture Tree

```text
backend/
├── .env                                    Variables d'environnements
├── docker-compose.yml                      Orchestre volumes DB/Spring/Images
├── Dockerfile                              Script de build et run via Docker
├── pom.xml                                 Configuration projet et dependances
└── src/
    ├── main/
    │   ├── java/com/example/finder/
    │   │   ├── FinderApplication.java          Point d'entree applicatif
    │   │   ├── config/
    │   │   │   ├── SecurityConfig.java         Regles de securites (API, Hashage)
    │   │   │   ├── JwtProperties.java          Prop. JWT (secret charge de l'env)
    │   │   │   ├── WebConfig.java              Permet de servir les images
    │   │   │   ├── RequestLoggingFilter.java   Log les requetes en entree
    │   │   │   ├── PaginationConfig.java       Regle pour les donnees paginees
    │   │   │   └── DatabaseSeeder.java         Composant de seed de la base de donnees
    │   │   ├── controller/
    │   │   │   ├── *Controller.java            Controlleurs pour routes User
    │   │   │   └── admin/
    │   │   │       └── *Controller.java        Controlleurs pour routes Admin
    │   │   ├── service/
    │   │   │   ├── *Service.java               Services appeles par les controlleurs
    │   │   ├── repository/
    │   │   │   ├── *Repository.java            Repository pour l'acces aux donnees via ORM
    │   │   │   └── specification/
    │   │   │       └── *Specifications.java    Spec. permettant un pattern builder sur requetes
    │   │   ├── model/
    │   │   │   ├── *.java (entities)           Classes representant les differentes entitees metiers
    │   │   │   └── enums/                      Valeurs minimale pour certaines entites (roles, status,...)
    │   │   ├── dto/
    │   │   │   ├── input/                      DTOs standardisant les body de requetes
    │   │   │   └── output/                     DTOs standardisant les donnees de reponses
    │   │   ├── response/                       Standardisation des formats de reponse selon les cas
    │   │   ├── exception/                      Exception personnalisee pour des cas d'erreur specifiques
    │   │   ├── seeders/                        Differentes classes pour seed des entites au lancement
    │   │   └── utils/                          Classes utilitaires (validations, sanitization, JWT, formating, ...)
    │   └── resources/
    │       ├── application.properties          Environnement pour le projet au runtime
    │       ├── application-dev.properties      Override d'environnement pour le projet en dev
    │       ├── application-test.properties     Override d'environnement pour le projet en test
    │       ├── application-prod.properties     Override d'environnement pour le projet en prod
    └── test/
        └── java/com/example/finder/
            ├── controller/                     Test d'integration pour controlleurs User
            │   └── admin/                      Test d'integration pour controlleurs Admin
            ├── utils/                          Test unitaires des classes utilitaires
            │   ├── jwt/
            │   └── validator/
            └── FinderApplicationTests.java     Test verifiant que l'application built fonctionne
```

## Layered Overview

The backend follows a layered Spring Boot architecture:

- Controllers: expose HTTP endpoints and map requests/responses.
- Services: implement business logic and orchestrate domain operations.
- Repositories: data-access layer using Spring Data JPA and specifications.
- Models and DTOs: persistence entities plus input/output contracts.
- Utils and config: cross-cutting concerns (JWT, validation, sanitization, security, pagination, logging).

## Main Package Layout

- `src/main/java/com/example/finder/config`: security and infrastructure configuration.
- `src/main/java/com/example/finder/controller`: public API endpoints.
- `src/main/java/com/example/finder/controller/admin`: admin API endpoints.
- `src/main/java/com/example/finder/service`: business services.
- `src/main/java/com/example/finder/repository`: repositories and query specifications.
- `src/main/java/com/example/finder/model`: entities and enums.
- `src/main/java/com/example/finder/dto`: request and response DTOs.
- `src/main/java/com/example/finder/utils`: helper classes and validation utilities.
- `src/main/resources`: environment-specific runtime properties.
