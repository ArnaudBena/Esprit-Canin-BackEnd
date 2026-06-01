# Esprit Canin — Backend

API REST de gestion d'un club d'éducation canine : utilisateurs, chiens, races, compétences, types de séances, séances et inscriptions.

Projet fil rouge réalisé dans le cadre du titre **Concepteur Développeur d'Applications (CDA – niveau 6)**, Metz Numeric School, promotion 2025-2026.

---

## Stack technique

| Couche | Technologie |
|---|---|
| Langage | Java 21 |
| Framework | Spring Boot 3.5 |
| Sécurité | Spring Security 6 + JWT (jjwt 0.9.1) |
| Persistance | Spring Data JPA / Hibernate 6 |
| Base de données | PostgreSQL 18 |
| Documentation API | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven |
| Utilitaires | Lombok |

---

## Architecture

Architecture 3-tiers, pattern **Controller → Service → DAO** :

```
controller/   Endpoints REST, routing, codes HTTP (aucune logique métier)
service/      Logique métier, règles de gestion, validations
dao/          Accès aux données (interfaces Spring Data JPA)
model/        Entités JPA (mapping ORM)
dto/          Objets de transport sur-mesure (ex : DashboardDto)
view/         Interfaces marqueuses pour les projections @JsonView
exception/    Exceptions métier (RuntimeException) levées par les services
config/       Configuration transverse (encodeur de mot de passe, gestion globale des exceptions)
security/     Spring Security : filtre JWT, UserDetails, configuration, annotations de rôle
utile/        Groupes de validation (OnCreate / OnUpdate)
```

Principes appliqués :
- **Back = source de vérité métier** : tri, filtres et règles côté serveur (requêtes JPQL `@Query`).
- **Gestion centralisée des erreurs** via `@RestControllerAdvice` (`GlobalExceptionInterceptor`).
- **Projections `@JsonView`** pour exposer uniquement les champs nécessaires à chaque vue (le mot de passe n'est jamais sérialisé).

---

## Sécurité

- Authentification **stateless** par **JWT** (`Authorization: Bearer <token>`).
- Mots de passe hachés avec **BCrypt** (jamais stockés en clair).
- Contrôle d'accès par rôle via annotations `@PreAuthorize` (`@IsAdmin`, `@IsCoach`, `@IsAdherent`).
- 3 rôles : **Admin** (gestion du club), **Coach** (séances et évaluations), **Adhérent** (compte, chiens, inscriptions).
- Endpoints publics : inscription (`/auth/inscription`) et connexion (`/auth/connexion`).
- CORS configuré pour le front Angular.

> ⚠️ Le secret JWT est externalisé via la variable d'environnement `JWT_SECRET` (voir `.env`). En production, utiliser un secret aléatoire d'au moins 256 bits, jamais commité.

---

## Prérequis

- **JDK 21**
- **Maven 3.9+** (ou le wrapper `./mvnw` fourni)
- **PostgreSQL 18** démarré et accessible

---

## Configuration

La configuration sensible est externalisée dans un fichier `.env` à la racine du projet (non commité).
Un modèle est fourni : **`.example.env`**.

1. Copier le modèle :
   ```bash
   cp .example.env .env
   ```
2. Adapter les valeurs à votre environnement :

   | Variable | Description | Exemple |
   |---|---|---|
   | `DB_HOST` | Hôte PostgreSQL | `localhost` |
   | `DB_PORT` | Port PostgreSQL | `5432` |
   | `DB_USER` | Utilisateur BDD | `root` |
   | `DB_PASSWORD` | Mot de passe BDD | `root` |
   | `DB_NAME` | Nom de la base | `db` |
   | `JWT_SECRET` | Secret de signature JWT | *(chaîne longue et aléatoire)* |
   | `DDL_AUTO` | Stratégie Hibernate | `create` (recrée le schéma) / `update` / `none` |
   | `SQL_FILE` | Plateforme du script SQL d'init | `init` (charge `data-init.sql`) |
   | `SQL_INIT_MODE` | Mode d'initialisation SQL | `always` / `never` |

> Avec `DDL_AUTO=create` + `SQL_INIT_MODE=always`, le schéma est recréé et le jeu de données de test (`src/main/resources/data-init.sql`) est rechargé à chaque démarrage. Passer à `update`/`never` pour conserver les données.

---

## Lancement

```bash
# Avec le wrapper Maven (recommandé)
./mvnw spring-boot:run

# ou avec Maven installé
mvn spring-boot:run
```

L'API démarre par défaut sur **http://localhost:8080**.

---

## Documentation de l'API

Une fois l'application démarrée :

- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **OpenAPI JSON** : http://localhost:8080/v3/api-docs

Chaque endpoint est documenté (résumé, description, codes de réponse) via les annotations OpenAPI.

---

## Tests

```bash
./mvnw test
```

---

## Structure des données

Entités principales : `Utilisateur`, `Role`, `Chien`, `Race`, `Competence`, `Seance`, `TypeSeance`, `Inscription` (table associative chien ↔ séance), `ChienCompetence` et `TypeSeanceCompetence` (tables associatives à clé composite).

Le jeu de données de test est défini dans `src/main/resources/data-init.sql`.
