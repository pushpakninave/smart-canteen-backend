# smart-canteen-backend

- JwtAuthenticationFilter -> (JwtTokenUtil, CustomUserDetailsService)

- JwtTokenUtil - modified extractAllClaims..removed depecated conventions.

## Questions

### Security

- **Basic Concepts**

    1. What is the purpose of the JwtTokenUtil class in a Spring Security application?

    2. Explain the difference between JWT tokens and traditional session-based authentication.

        ## JWT Tokens vs. Session-Based Authentication

        | Feature                | JWT Tokens                                    | Session-Based Authentication             |
        |------------------------|-----------------------------------------------|------------------------------------------|
        | **State Management**   | Stateless (self-contained tokens)             | Stateful (server stores session data)    |
        | **Storage Location**   | Client-side (cookie/localStorage)             | Server-side (database/memory)            |
        | **Scalability**        | Excellent (no server-side storage needed)     | Requires session replication             |
        | **Performance**        | Faster (no database lookups)                  | Slower (requires session validation)     |
        | **Expiration Handling**| Built into token (exp claim)                  | Server-managed session timeout           |
        | **Security Aspects**   | Signed tokens (needs HTTPS)                   | Opaque session IDs + CSRF protection     |
        | **Use Cases**          | APIs, microservices, mobile apps              | Traditional server-rendered web apps     |
        | **Payload Flexibility**| Can contain custom claims                     | Limited to session ID                    |
        | **Revocation**         | Difficult (until expiration)                  | Immediate (server can invalidate)        |
        | **Cross-Domain Usage** | Easy (CORS-friendly)                          | Requires additional configuration        |

    3. What are the main components of a JWT token and how does this class interact with them?

- **Implementation Details**

    1. Why is the SECRET_KEY stored as a configuration property rather than hardcoded?

    2. How does the getSigningKey() method work and why is base64 decoding necessary?

    3. Explain the token generation process in this class from generateToken() to createToken().

    4. What security risks would exist if we didn't validate the token's expiration date?

- **Method-Specific Questions**

    1. Why is the extractClaim() method generic and how does it promote code reuse?

    2. What would happen if validateJwtToken() only checked the signature but didn't verify expiration?

    3. How does isTokenValid() combine both user verification and expiration checking?

- **Error Handling & Security**

    1. What potential security vulnerabilities should be considered when using this JWT implementation?

    2. Why is the error message in validateJwtToken() logged but not thrown as an exception?

    3. How would you modify this class to support token refresh functionality?

- **Performance & Scalability**

    1. How does this stateless JWT approach compare to stateful session management in terms of scalability?

    2. What would be the impact of making the EXPIRATION time very long vs very short?

- **Advanced Scenarios**

    1. How would you extend this class to support additional claims in the JWT token?

    2. What changes would be needed to support multiple signing algorithms (not just HS256)?

    3. How could you implement token invalidation before expiration with this JWT approach?

- **Best Practices**

    1. Why is it important to keep the SECRET_KEY secure and how would you manage it in production?

    2. What are the limitations of this JWT implementation that developers should be aware of?

- **Testing Questions**

    1. How would you unit test the isTokenExpired() method?

    2. What test cases would you write for the validateJwtToken() method?

    3. How would you mock dependencies when testing the generateToken() method?

- **Real-World Scenarios**

    1. How would you handle a situation where the SECRET_KEY needs to be rotated?

    2. What would you do if you discovered tokens were being generated with incorrect expiration times?


### `AuthEntryPointJwt`

This is a Spring Security component that handles unauthorized access attempts in your application. It implements Spring's AuthenticationEntryPoint interface, which is the entry point for handling authentication failures.

- `The commence() Method`: This is the heart of the class that gets called when...An **unauthenticated user** tries to access a secured resource or **Authentication fails** (invalid/expired token, etc.)


## UML

```
┌───────────────────────────────────────┐
│        <<interface>>                  │
│          UserDetails                  │
├───────────────────────────────────────┤
│ + getAuthorities(): Collection        │
│ + getPassword(): String               │
│ + getUsername(): String               │
│ + isAccountNonExpired(): boolean      │
│ + isAccountNonLocked(): boolean       │
│ + isCredentialsNonExpired(): boolean  │
│ + isEnabled(): boolean                │
└───────────────────────────────────────┘
                     ▲
                     │ implements
                     │
┌───────────────────────────────────────┐
│          UserDetailsImpl              │
├───────────────────────────────────────┤
│ - id: Long                            │
│ - username: String                    │
│ - email: String                       │
│ - password: String                    ││ - authorities: Collection<GrantedAuthority> │
├───────────────────────────────────────┤
│ + UserDetailsImpl(id, username,       │
│   email, password, authorities)       │
│ + build(user, authorities):           │
│   UserDetailsImpl                     │
│ + equals(Object): boolean             │
│ + hashCode(): int                     │
└───────────────────────────────────────┘
```

### modifications : 

1. Order - `entity`
- not storing totalPrice in db
2. `UserDetailsImpl` - need to understand