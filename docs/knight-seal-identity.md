# Knight Seal and Tavern identity

Taverns can inject `dev.ioza.kingdom.contracts.identity.KingdomIdentityProvider` when Tavern Lock is enabled. The provider reads the authenticated JWT only, never a raw request header. Use `subject()` or `require()`, and check `isKnight()` before granting user-only access. The provider is for servlet-based Taverns and the active request thread.

The password-based Knight Seal endpoint is registered by default. Authentication fails closed when its dedicated client is not configured. Configure a dedicated confidential Keycloak client with Direct Access Grants enabled (not the Herald client), and supply:

```yaml
kingdom:
  realm:
    keycloak:
      knight-seal:
        client-id: ${KNIGHT_SEAL_CLIENT_ID}
        client-secret: ${KNIGHT_SEAL_CLIENT_SECRET}
```

POST /seal/knight with JSON username and password over HTTPS returns the existing SealResponse, with a Knight Parchment. No Passport cookie is created. Audience, Herald and Vassal Seal remain unchanged. Never log or commit credentials. Restrict this endpoint to controlled testing, rate limit it at the ingress, and use Authorization Code with PKCE for production clients; password grant is discouraged and cannot support all MFA flows.

Existing Knight subject semantics are username, not Keycloak UUID. Do not treat a Vassal subject as a human user. Existing Knight roles/decrees behavior is unchanged.
