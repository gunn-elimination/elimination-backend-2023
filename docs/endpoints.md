### Authenticating
Visit `/oauth2/authorization/google` to authenticate.  

### Authenticated endpoints
These endpoints require the current session to be authenticated.

#### /game/code
Gets the current authenticated user's elimination code

#### /game/eliminate?code=<code>
Attempts to eliminate the current authenticated user from the game with the given code.

#### /me
Gets the current authenticated user's information.

### Unauthenticated endpoints
These endpoints do not require the current session to be authenticated.

#### /scoreboard?limit=[limit=20]
Returns the top [limit] players in the game, ordered by the number of eliminations they have achieved.