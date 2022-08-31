package net.gunn.elimination.auth;

import net.gunn.elimination.model.EliminationUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

class EliminationOauthAuthenticationImpl extends DefaultOidcUser implements EliminationAuthentication {
   private final EliminationUser user;
   private final OidcUser original;

   public EliminationOauthAuthenticationImpl(EliminationUser user, OidcUser original) {
	  super(user.getRoles().stream().map(Object::toString).map(SimpleGrantedAuthority::new).toList(), original.getIdToken(), original.getUserInfo());
	  this.user = user;
	  this.original = original;
   }

   public EliminationUser user() {
	  return user;
   }

   public OidcUser original() {
	  return original;
   }
}
