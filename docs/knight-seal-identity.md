# Knight Seal and Tavern identity

Taverns can inject `dev.ioza.kingdom.contracts.identity.KingdomIdentityProvider` when Tavern Lock is enabled. The provider reads the authenticated JWT only, never a raw request header. Use `subject()` or `require()`, and check `isKnight()` before granting user-only access. The provider is for servlet-based Taverns and the active request thread.

The password-based Knight Seal endpoint is registered by default. It reuses the existing confidential Herald client. Enable Direct Access Grants on Herald in Keycloak; no additional Bastion properties or credentials are required.

POST /seal/knight with JSON username and password over HTTPS returns the existing SealResponse, with a Knight Parchment. No Passport cookie is created. Audience and Vassal Seal code paths remain unchanged; Herald now also permits Direct Access Grants. Never log or commit credentials. Restrict this endpoint to controlled testing, rate limit it at the ingress, and use Authorization Code with PKCE for production clients; password grant is discouraged and cannot support all MFA flows.

Existing Knight subject semantics are username, not Keycloak UUID. Do not treat a Vassal subject as a human user. Existing Knight roles/decrees behavior is unchanged.
