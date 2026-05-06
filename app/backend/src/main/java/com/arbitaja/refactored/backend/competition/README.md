# Competition Management (Hexagonal)

This module introduces competition management using the same hexagonal style as PAM.

## Structure

- `adapter/in/web` - REST controllers and request/response DTOs
- `core/port/in` - use-case interfaces for reads/writes
- `core/application` - use-case implementations
- `core/domain` - domain models and domain exceptions
- `core/port/out` - output ports
- `adapter/out/persistence` - adapters bridging legacy repositories/entities

## Runtime toggle

Competition mode is controlled with:

- `arbitaja.competition.mode=legacy` (default)
- `arbitaja.competition.mode=hex`

Related profile files:

- `src/main/resources/application-competition-legacy.yaml`
- `src/main/resources/application-competition-hex.yaml`

## Endpoints in hex mode

Base path: `/v2/competition`

- `GET /v2/competition`
- `GET /v2/competition/{id}`
- `GET /v2/competition/by-name?name=...`
- `POST /v2/competition`
- `PUT /v2/competition/{id}`
- `POST /v2/competition/{competitionId}/competitors/{competitorId}`
- `PUT /v2/competition/{competitionId}/competitors`
- `DELETE /v2/competition/{competitionId}/competitors/{competitorId}`
- `DELETE /v2/competition/{id}`

Competitor management lives under `/v2/competitor`:

- `POST /v2/competitor`
- `PUT /v2/competitor/{id}`

School management lives under `/v2/schools`:

- `GET /v2/schools`
- `GET /v2/schools/{id}`
- `POST /v2/schools`
- `PUT /v2/schools/{id}`
- `DELETE /v2/schools/{id}`

