# Bastion-managed tavern catalog (draft)

Bastion is authoritative for route ownership and allowed authentication methods.

## Administrative provisioning
`POST /internal/kingdom/tavern-definitions` requires HTTP Basic `registry-admin` and the `KINGDOM_REGISTRY_ADMIN_PASSWORD` container environment variable.

Sample payload:
```json
{
  "tavernId": "bank",
  "domain": "bank.ioza.dev",
  "domainPath": "/**",
  "context": "/bank",
  "capabilities": ["ui"],
  "publicPaths": ["/assets/**"],
  "enabled": true
}
```
When `allowedAccess` is absent or empty, Bastion stores `["KNIGHT"]`. For a Tavern that may accept Carriage, configure `["KNIGHT", "CARRIAGE"]` explicitly. Do not give Bank API Carriage.

## Replica registration
`POST /internal/kingdom/taverns` requires Tavern service credentials. New payload:
```json
{
  "tavernId": "bank",
  "instanceId": "bank-550e8400-e29b-41d4-a716-446655440000",
  "target": "http://bank-front:80",
  "healthPath": "/",
  "version": "1.0.0",
  "startedAt": "2026-11-24T15:00:00Z"
}
```
The Emissary generates `tavernId-UUID` once per process startup. Bastion **ignores any caller-provided domain, context, public paths, capabilities and security policy**, constructing the trusted manifest from its catalog.

## Cautions before deploying
- Breaking wire change: legacy curl registrations that POST the full manifest now need to send this instance-only payload, and every tavern needs to be pre-provisioned.
- Existing historical `kingdom_taverns` documents are **not migrated**. On restart, only manifests matching an enabled catalog definition are restored to the in-memory registry; orphan records remain in MongoDB.
- The existing shared `tavern` HTTP Basic password does not prove which logical `tavernId` the caller owns. Configure unique per-tavern credentials / mTLS or identity-bound service JWTs **before production**.
- Catalog ownership conflict checks currently use a read-before-insert check. Without atomic ownership reservations, concurrent updates can race; use unique keys/transactions when multiple Bastion writers exist.
- Gateway still uses one representative instance (no round robin or failover). Health-aware load balancing, instance expiration and logical/physical collection separation remain pending.
- Run Maven compilation, tests, integration tests and SPA context-path verification; no CI validation has been completed here.
