package net.gunn.elimination.auth;

import net.gunn.elimination.model.EliminationUser;
import org.springframework.security.core.AuthenticatedPrincipal;

public interface EliminationAuthentication extends AuthenticatedPrincipal {
   EliminationUser user();
}
