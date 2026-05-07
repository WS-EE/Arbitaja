# Scoring (Hexagonal)

Hexagonal-architecture port of the legacy `com.arbitaja.backend.competitions.scorings`
module, following the same style as PAM and Competition.

## Structure

- `adapter/in/web` - REST controllers, request/response DTOs, AOP guard, exception handler
- `core/port/in` - use-case interfaces (criterion, history, dashboard, security)
- `core/application` - use-case implementations
- `core/domain` - domain models and domain exceptions
- `core/port/out` - output ports (criterion, history, lookup, security)
- `adapter/out/persistence` - JPA persistence adapters (with their own slim entities for
  competition/competitor lookups so the module is decoupled from other aggregates)
- `adapter/out/security` - delegates permission checks to PAM

## Runtime toggle

Scoring mode is controlled with:

- `arbitaja.scoring.mode=legacy` (legacy controllers)
- `arbitaja.scoring.mode=hex` (default in `application.yaml`)

Related profile files:

- `src/main/resources/application-scoring-hex.yaml`
- `src/main/resources/application-scoring-legacy.yaml`

## Endpoints in hex mode

### Scoring criteria — `/v2/scoring/criteria`

- `GET /v2/scoring/criteria`
- `GET /v2/scoring/criteria/{id}`
- `GET /v2/scoring/criteria/by-competition/{competitionId}`
- `POST /v2/scoring/criteria`
- `PUT /v2/scoring/criteria/{id}`
- `DELETE /v2/scoring/criteria/{id}`
- `POST /v2/scoring/criteria/{criterionId}/competitions/{competitionId}`

### Recording results — `/v2/scoring/history`

- `POST /v2/scoring/history`

### Dashboard — `/v2/scoring/dashboard`

- `GET /v2/scoring/dashboard/competition/{competitionId}/history`
- `GET /v2/scoring/dashboard/competition/{competitionId}/criteria`
- `GET /v2/scoring/dashboard/competition/{competitionId}/criteria/competitor/{competitorId}`

## Performance notes

The dashboard read paths replace the legacy N+1 traversal with three single-query
operations on `ScoringHistoryJpaRepository`:

- `findHistoryForCompetition` — one query for the running-total chart, ordered for a
  linear-time client-side aggregation.
- `findLatestPerCompetitorAndCriterion` — one query for the per-criterion result grid.
- `findLatestPerCriterionForCompetitor` — same idea scoped to a single competitor.
