# Finder App - API

## Presentation

This repo is related to a training course brief involving creating an App intended to let users post announces for lost and found items.
The brief itself is based on three components :

- Headless API : Providing authentification and data crud, the chosen stack for this part is Spring Boot and Postgresql inside docker containers.
- Client : App the users will be interacting with for the intended features. Will be made using React Native.
- Back office : Interface restricted to admin only and mainly intended for moderation purpose. Will be made with Angular.

This repo covers the subproject related to the API implementation.

## Architecture (Tree View)

```text
backend/
├── .env                                    Variables d'environnements
├── docker-compose.yml                      Orchestre volumes DB/Spring/Images
├── Dockerfile                              Script de build et run via Docker
├── pom.xml                                 Configuration projet et dépendances
└── src/
    ├── main/
    │   ├── java/com/example/finder/
    │   │   ├── FinderApplication.java          Point d'entrée applicatif
    │   │   ├── config/
    │   │   │   ├── SecurityConfig.java         Règles de sécurités (API, Hashage)
    │   │   │   ├── JwtProperties.java          Prop. JWT (secret chargé de l'env)
    │   │   │   ├── WebConfig.java              Permet de servir les images
    │   │   │   ├── RequestLoggingFilter.java   Log les requêtes en entrée
    │   │   │   ├── PaginationConfig.java       Règle pour les données paginées
    │   │   │   └── DatabaseSeeder.java         Composant de seed de la base de données
    │   │   ├── controller/
    │   │   │   ├── *Controller.java            Controlleurs pour routes User
    │   │   │   └── admin/
    │   │   │       └── *Controller.java        Controlleurs pour routes Admin
    │   │   ├── service/
    │   │   │   ├── *Service.java               Services appelés par les controlleurs
    │   │   ├── repository/
    │   │   │   ├── *Repository.java            Repository pour l'accès aux données via ORM
    │   │   │   └── specification/
    │   │   │       └── *Specifications.java    Spec. permettant un pattern builder sur requêtes
    │   │   ├── model/
    │   │   │   ├── *.java (entities)           Classes représentant les différentes entitées métiers
    │   │   │   └── enums/                      Valeurs minimale pour certaines entités (roles, status,...)
    │   │   ├── dto/
    │   │   │   ├── input/                      DTOs standardisant les body de requêtes
    │   │   │   └── output/                     DTOs standardisant les données de réponses
    │   │   ├── response/                       Standardisation des formats de réponse selon les cas
    │   │   ├── exception/                      Exception personalisée pour des cas d'erreur spécifiques
    │   │   ├── seeders/                        Différentes classes pour seed des entités au lancement
    │   │   └── utils/                          Classes utilitaires (validations, sanitization, JWT, formating, ...)
    │   └── resources/
    │       ├── application.properties          Environnement pour le projet au runtime
    │       ├── application-dev.properties      Override d'environnement pour le projet en dev
    │       ├── application-test.properties     Override d'environnement pour le projet en test
    │       ├── application-prod.properties     Override d'environnement pour le projet en prod
    └── test/
        └── java/com/example/finder/
            ├── controller/                     Test d'intégration pour controlleurs User
            │   └── admin/                      Test d'intégration pour controlleurs Admin
            ├── utils/                          Test unitaires des classes utilitaires
            │   ├── jwt/
            │   └── validator/
            └── FinderApplicationTests.java     Test vérifiant que l'application built fonctionne
```

## Layered Overview

- Controllers handle HTTP endpoints and request/response orchestration.
- Services implement business logic and coordinate repositories/utilities.
- Repositories handle data access (Spring Data JPA + specifications).
- Models and DTOs define persistence and API contracts.
- Utils provide cross-cutting helpers (JWT, validation, sanitization, string/image helpers).
