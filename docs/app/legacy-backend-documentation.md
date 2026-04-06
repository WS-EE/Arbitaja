# Legacy Backend Documentation (`com.arbitaja.backend`)

This document describes the legacy backend under:

- `app/backend/src/main/java/com/arbitaja/backend`

It covers responsibilities, architecture roles, key APIs, data model objects, repositories, and known implementation notes.

## 1. System Overview

- **Stack**: Spring Boot, Spring MVC, Spring Security, Spring Data JPA, Hibernate.
- **API style**: REST endpoints (mostly under `/v1/...`) with `ResponseEntity` responses.
- **Security model**:
  - Form login at `/login-user`.
  - Session-based authentication with remember-me.
  - Method-level authorization via `@PreAuthorize` and authorities such as `admin` and `basic`.
- **Persistence model**:
  - JPA entities with mostly direct table mapping.
  - Several native SQL queries in repositories.
- **Error handling**:
  - `GlobalExceptionHandler` maps domain/validation/runtime exceptions to HTTP responses.

## 2. Package Map

- `com.arbitaja.backend`: bootstrap, health, global exception handling.
- `com.arbitaja.backend.security`: security config, login endpoint, auth filters, OpenAPI security setup.
- `com.arbitaja.backend.users`: user lifecycle, signups, roles, permissions, password changes.
- `com.arbitaja.backend.competitors`: competitors, schools, competitor-to-competition mapping.
- `com.arbitaja.backend.competitions`: competition CRUD and response shaping.
- `com.arbitaja.backend.competitions.scorings`: scoring criteria, scoring history, dashboard aggregation.
- `com.arbitaja.backend.agents`: entities for scoring agents/proxies/transports/scripts.

---

## 3. Root Package (`com.arbitaja.backend`)

### `ArbitajaBackendApplication.java`
- **Role**: Spring Boot entrypoint.
- **Key behavior**:
  - Starts application via `SpringApplication.run(...)`.
  - Scans both legacy and refactored packages:
    - `com.arbitaja.backend`
    - `com.arbitaja.refactored.backend`
- **Note**: Legacy and refactored modules are active in same runtime context.

### `HealthController.java`
- **Role**: Health endpoint for liveness checks.
- **Endpoint**:
  - `GET /health` -> `200 OK` with empty body.
- **Usage**: Infrastructure readiness/liveness probe.

### `GlobalExceptionHandler.java`
- **Role**: Centralized exception-to-response mapping (`@ControllerAdvice`).
- **Handled exception categories**:
  - DB violations, bean validation, illegal args/state, unauthorized, duplicate, not found, JSON errors, fallback `Exception`.
  - Also maps refactored-domain unauthorized exception:
    - `com.arbitaja.refactored.backend.pam.core.domain.exception.UnauthorizedException`.
- **Nested types**:
  - `ErrorResponse` record.
  - `ApiException` and subclasses: `NotFoundException`, `DuplicateException`, `UnauthorizedException`.

---

## 4. Security Package (`com.arbitaja.backend.security`)

### `SecurityConfig.java`
- **Role**: Main Spring Security configuration.
- **Key configuration points**:
  - Password encoder: BCrypt (strength 10).
  - Custom `UserDetailsService` (`MyUserDetailsService`).
  - CORS config with configured frontend URL + localhost.
  - Security chain includes `LoginLoggingFilter`.
  - Authorization: `publicApis` permitted, all others authenticated.
  - Form login:
    - Page: `/login`
    - Processing: `/login-user`
  - Remember-me and logout configured.
  - CSRF disabled.

### `OpenApiConfig.java`
- **Role**: OpenAPI (Swagger) setup.
- **Key behavior**:
  - Adds server URL from `BASE_URL + BASE_PATH`.
  - Defines HTTP `basicAuth` security scheme.

### `MyUserDetailsService.java`
- **Role**: Loads user and authorities for Spring Security authentication.
- **Key methods**:
  - `loadUserByUsername(String username)`.
  - Resolves authorities from permission records (`PermissionRepository.findPermissionsByUserId(...)`).

### `LoginLoggingFilter.java`
- **Role**: Logs login attempts (`/login-user`, POST).
- **Behavior**:
  - Reads `username` and `password` request params and logs them.
- **Risk note**: Logging plaintext passwords is a security/privacy risk.

### `AuthController.java`
- **Role**: Login/logout response endpoint.
- **Endpoint**:
  - `GET /login`
- **Behavior**:
  - On successful auth: returns `UserProfileResponse` for authenticated user.
  - On logout mode (`logout=true`): returns `204` with text message.
  - On missing auth or error cases: returns `400` or throws `IllegalArgumentException`.

### `ApiKeyFilter.java`
- **Role**: API key gate for scoring history ingestion endpoint.
- **Behavior**:
  - For URIs starting with `/competition/criteria/history/add`, checks header `X-API-KEY` against configured `app.API_KEY`.
  - Rejects invalid/missing key with `401`.

---

## 5. Users Package (`com.arbitaja.backend.users`)

### 5.1 APIs

#### `APIs/UserController.java`
- **Role**: User/account HTTP API under `/v1/user`.
- **Main endpoints**:
  - `GET /profile/get` (self or admin querying other user).
  - `GET /profile/all` (admin).
  - `DELETE /profile/delete` (admin).
  - `PUT /profile/edit` (basic/admin with constraints).
  - `POST /signup/create`.
  - `POST /signup/approve` (admin).
  - `DELETE /signup/approve` (admin; decline signup).
  - `GET /signup/get` (admin).
  - `PUT /profile/update_password`.
  - `POST /create` (admin; create immediate user).
- **Security**: method-level authorization via `@PreAuthorize`.

#### `APIs/UserService.java`
- **Role**: User domain service and orchestration layer.
- **Core responsibilities**:
  - User retrieval (`getUserByUsername`, `getUserById`).
  - Profile mapping to `UserProfileResponse` including recursive roles/permissions fetch.
  - Profile updates with authorization checks.
  - Signup workflow (create, approve, decline).
  - Password updates with old password verification for non-admin users.
  - Direct user creation with role assignment (`user`, optional `admin`).
- **Important collaborators**:
  - `UserRepository`, `SignupUserRepository`, `RoleRepository`, `PermissionRepository`, `UserRoleRepository`, `SchoolRepository`, `PersonalDataRepository`, `PasswordEncoder`.

#### `APIs/responses/UserProfileResponse.java`
- **Role**: API DTO for user profile responses.
- **Structure**:
  - Root: `id`, `username`, `roles`, `permissions`, `personal_data`.
  - Nested `PersonalDataResponse`, nested `SchoolResponse`.

#### `APIs/requests/PasswordChangeRequest.java`
- **Role**: Request DTO for password update.
- **Fields**:
  - `id`, `old_password`, `new_password`.

### 5.2 Data Objects

#### `dataobjects/User.java`
- **Role**: Main user entity (`"user"` table).
- **Relations**:
  - `default_token` (`Api_token`), `personal_data`, `apiTokens`, `competitions`, `user_roles`.

#### `dataobjects/SignupUser.java`
- **Role**: Pre-approved signup request entity.
- **Fields**:
  - `username`, `salted_password`, `personal_data`, `isApproved`, `createdAt`.

#### `dataobjects/Role.java`
- **Role**: Role entity.
- **Relations**:
  - Parent/child role hierarchy (`Role_relation`), role permissions, user roles.

#### `dataobjects/Permission.java`
- **Role**: Permission entity.
- **Fields**:
  - `name`, `key`, `key_object`.

#### `dataobjects/Api_token.java`
- **Role**: API token entity bound to user.
- **Relations**:
  - Owner `User`, linked `ScoringAgent` records.

#### `dataobjects/Role_permissions.java`
- **Role**: Role-to-permission relation with JSON ACL.
- **Fields**:
  - `permission`, `role`, `key_object_acl` (`jsonb`).

#### `dataobjects/Role_relation.java`
- **Role**: Parent-child role hierarchy edge.

#### `dataobjects/User_role.java`
- **Role**: User-to-role assignment relation.
- **Fields**:
  - `user`, `role`, `created_at`.

### 5.3 Repositories

#### `repositories/UserRepository.java`
- **Role**: User persistence.
- **Notable methods**:
  - `findByUsername`, `findUserByUsername`.
  - `updateUser(User user)` via native UPSERT SQL.

#### `repositories/RoleRepository.java`
- **Role**: Role persistence and recursive role traversal.
- **Notable method**:
  - `findRolesByUserId(int userId)` using recursive CTE over `role_relation`.

#### `repositories/PermissionRepository.java`
- **Role**: Permission lookup including effective permissions per user.
- **Notable method**:
  - `findPermissionsByUserId(int userId)` using recursive role hierarchy CTE.

#### `repositories/UserRoleRepository.java`
- **Role**: User-role mapping persistence.

#### `repositories/ApiTokenRepository.java`
- **Role**: API token lookup (`findByToken`).

#### `repositories/RolePermissionsRepository.java`
- **Role**: Role-permission mapping persistence.

#### `repositories/RoleRelationRepository.java`
- **Role**: Role hierarchy edge persistence.

#### `repositories/SignupUserRepository.java`
- **Role**: Signup user persistence and username existence check.

---

## 6. Competitors Package (`com.arbitaja.backend.competitors`)

### 6.1 APIs

#### `APIs/CompetitorController.java`
- **Role**: Competitor management API under `/v1/competitor`.
- **Main endpoints**:
  - Add/edit/delete competitor.
  - Get one/all competitors.
  - Add/remove competitor to/from competition.
  - Get all competitors in competition.
  - Get all competitions for competitor.

#### `APIs/CompetitorService.java`
- **Role**: Competitor domain operations.
- **Core responsibilities**:
  - Add/edit competitor with linked or newly created `Personal_data`.
  - CRUD operations.
  - Manage `competitor_competition` join records.
  - Resolve competitions for competitor and map via `CompetitionService`.

#### `APIs/SchoolController.java`
- **Role**: School API under `/v1/school`.
- **Endpoints**:
  - `GET /all/get`, `GET /get`, `POST /register`, `DELETE /register`, `PUT /edit`.

#### `APIs/SchoolService.java`
- **Role**: School business logic.
- **Core responsibilities**:
  - Add/update/delete/get school.
  - Validation and not-found handling.

#### `APIs/CompetitionCompetitorWrapper.java`
- **Role**: Request wrapper DTO for add-to-competition operations.
- **Fields**:
  - `Competition competition`, `Competitor competitor`.

#### `APIs/responses/CompetitionResponse.java`
- **Role**: Reusable competition response DTO used by competition/competitor APIs.
- **Contains nested DTOs**:
  - `Organizer_idResp`, `CompetitorResp`, `Scoring_groups_structure_resp`.

### 6.2 Data Objects

#### `dataobjects/Competitor.java`
- **Role**: Competitor entity.
- **Fields/relations**:
  - `public_display_name_type`, `alias`, `personal_data`, relations to competition/scoring groups.

#### `dataobjects/Competitor_competition.java`
- **Role**: Join entity between competitor and competition.

#### `dataobjects/Personal_data.java`
- **Role**: Personal profile data shared by users/competitors.
- **Fields**:
  - `full_name`, `email`, `school`, `created_at`.

#### `dataobjects/School.java`
- **Role**: School reference entity.

### 6.3 Repositories

#### `repositories/CompetitorRepository.java`
- **Role**: Competitor persistence.
- **Notable methods**:
  - `findByCompetitionId(Integer)` via native join query.
  - `findByAlias(String)`.

#### `repositories/CompetitorCompetitionRepository.java`
- **Role**: Competitor-competition mapping persistence.

#### `repositories/PersonalDataRepository.java`
- **Role**: Personal data persistence.
- **Notable method**:
  - `updateOrCreatePersonal_data(...)` via native UPSERT.

#### `repositories/SchoolRepository.java`
- **Role**: School persistence.
- **Notable method**:
  - `updateSchool(...)` via native UPSERT-like SQL.

---

## 7. Competitions Package (`com.arbitaja.backend.competitions`)

### 7.1 APIs

#### `APIs/CompetitionController.java`
- **Role**: Competition API under `/v1/competition`.
- **Endpoints**:
  - `GET /all/get`, `GET /get`, `POST /add`, `PUT /edit`, `DELETE /delete`.
- **Security**:
  - Mutating endpoints require `admin` authority.

#### `APIs/CompetitionService.java`
- **Role**: Competition domain service.
- **Core responsibilities**:
  - CRUD operations for competitions.
  - Map entity to `CompetitionResponse` with organizer, competitors, scoring groups.
  - Resolve organizer by username or id.

### 7.2 Data Objects

#### `dataobjects/Competition.java`
- **Role**: Competition entity.
- **Fields**:
  - Name/time windows, showtime/publication flags, organizer, main scoring group structure.

### 7.3 Repositories

#### `repositories/CompetitionRepository.java`
- **Role**: Competition persistence.
- **Notable methods**:
  - `findByid`, `findByName`.
  - `findByCompetitorId(...)` via native join query.

---

## 8. Scoring Package (`com.arbitaja.backend.competitions.scorings`)

### 8.1 APIs

#### `APIs/CompetitionScoringController.java`
- **Role**: Scoring history/criteria API endpoints under `/v1`.
- **Endpoints**:
  - `POST /competition/criteria/history/add`
  - `GET /dashboard/competition/history`
  - `GET /dashboard/competition/criteria`
  - `GET /dashboard/competition/criteria/competitor`

#### `APIs/CompetitionScoringService.java`
- **Role**: Scoring aggregation and scoring history business logic.
- **Core responsibilities**:
  - Add scoring history with validation (competition active, competitor in competition, points range).
  - Compute current criteria scores per competitor.
  - Build dashboard time-series score responses.
  - Access control checks for score visibility (`publish_scores` or admin).
  - Derive public display alias depending on competitor display mode.

#### `APIs/ScoringCriterionController.java`
- **Role**: Scoring criterion CRUD + competition assignment endpoints.
- **Endpoints**:
  - Add/get/get-all/delete/update criteria.
  - Add criterion to competition.

#### `APIs/ScoringCriterionService.java`
- **Role**: Scoring criterion business service.
- **Core responsibilities**:
  - Create/update/delete criterion.
  - Optionally attach new criterion to a competition.
  - Resolve template criterion references.

#### `APIs/ScoringCriterionDeserializer.java`
- **Role**: Custom Jackson deserializer for criterion references by id.

#### `APIs/ScoringCriterionSerializer.java`
- **Role**: Custom Jackson serializer for criterion references as id only.

#### `APIs/Request/ScoringCriteriaAdd.java`
- **Role**: Request DTO for creating scoring criteria.

#### `APIs/Response/CompetitionScoringResponse.java`
- **Role**: DTOs for dashboard response (`Dashboard`, `Competitor`, `Result`).

#### `APIs/Response/ScoringCriteriaResultForCompetitors.java`
- **Role**: DTOs for criteria-by-competitor snapshot.

### 8.2 Data Objects

#### `dataobjects/ScoringCriterion.java`
- **Role**: Main scoring criterion entity.
- **Fields/relations**:
  - Optional `scoringHost`, optional template criterion, criteria metadata flags.
  - Relations to logical groups, agents, history, competition mappings.

#### `dataobjects/CompetitionScoringCriterion.java`
- **Role**: Join entity linking competitions and scoring criteria.

#### `dataobjects/ScoringHistory.java`
- **Role**: Event/history entity for scoring submissions.
- **Fields**:
  - Competitor, competition, host, criterion, points, result, timestamps, submitting user.

#### `dataobjects/ScoringHost.java`
- **Role**: Scoring host entity.
- **Fields/relations**:
  - Network identity, template relation, generalized/template flags, relations to links/agents/criteria/history.

#### `dataobjects/CriteriaDependency.java`
- **Role**: Defines criterion dependency graph edges.

#### `dataobjects/LogicalGroupLink.java`
- **Role**: Link entity between logical groups, hosts, and criteria.

#### `dataobjects/Scoring_groups_structure.java`
- **Role**: Hierarchical scoring group structure for competition/competitor scope.
- **Fields**:
  - Parent/child relation, group type, dynamic variables (`jsonb`).

#### `dataobjects/ScoringLogicalGroup.java`
- **Role**: Logical grouping entity for scoring semantics.

### 8.3 Repositories

#### `repositories/CompetitionScoringCriterionRepository.java`
- **Role**: Query criterion set for a competition.

#### `repositories/ScoringCriterionRepository.java`
- **Role**: Basic criterion CRUD.

#### `repositories/ScoringHistoryRepository.java`
- **Role**: Scoring history query repository.
- **Notable queries**:
  - By competition+competitor up to showtime.
  - Latest result by competition+competitor+criteria.

#### `repositories/ScoringGroupsStructureRepository.java`
- **Role**: Scoring groups retrieval for competition using native SQL.

#### `repositories/ScoringHostRepository.java`
- **Role**: Placeholder interface (currently no methods, not extending Spring repository).

---

## 9. Agents Package (`com.arbitaja.backend.agents`)

### 9.1 Data Objects

#### `dataobjects/AgentProxy.java`
- **Role**: Proxy/gateway definition for agent communication.
- **Fields**:
  - `ip`, `name`, `competition`, `token`, `authenticationType`.

#### `dataobjects/ScoringAgent.java`
- **Role**: Agent runtime configuration entry bound to host/criterion/transport/script/token/proxy.
- **Fields**:
  - `authenticationType`, `agentType`, `customApiEndpoint`, relation references.

#### `dataobjects/ScoringAgentTransport.java`
- **Role**: Transport protocol config for scoring agents.
- **Fields**:
  - `name`, `tcpPort`, `sshKey`.

#### `dataobjects/Script.java`
- **Role**: Script entity used by scoring agents.
- **Fields**:
  - `name`, `type`, `script`.

### 9.2 Repositories

#### `repositories/ScriptRepository.java`
- **Role**: Placeholder interface (currently empty; not yet wired to Spring Data).

---

## 10. Legacy Design Notes and Technical Debt

- Naming style is inconsistent (`snake_case` Java fields/classes such as `Role_permissions`, `Personal_data`, `Scoring_groups_structure`).
- Some repository interfaces are placeholders and not standard Spring repositories (`ScriptRepository`, `ScoringHostRepository`).
- Several methods return `ResponseEntity<?>` broadly, making API contracts less strict.
- Some native queries and update methods bypass type-safe entity management and can be harder to evolve.
- Sensitive credential logging exists in `LoginLoggingFilter`.
- Legacy and refactored modules are both scanned by the same application class; boundary ownership should stay explicit during migration.

## 11. Migration Guidance (Legacy -> Refactored)

- Treat this package as **legacy compatibility layer**.
- Prefer adding new behavior in refactored modules under `com.arbitaja.refactored.backend`.
- When touching legacy code:
  - keep endpoint contracts stable,
  - add tests around changed behavior,
  - avoid broad renames unless migration mapping is documented.

