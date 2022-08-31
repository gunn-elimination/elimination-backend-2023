package net.gunn.elimination.auth;

import net.gunn.elimination.repository.RoleRepository;
import net.gunn.elimination.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import javax.servlet.*;
import java.io.IOException;

@Service
class RoleReloader implements Filter {
   private final UserRepository userRepository;

   public RoleReloader(UserRepository userRepository) {
	  this.userRepository = userRepository;
   }

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
	  var ctx = SecurityContextHolder.getContext();

	  if (ctx.getAuthentication().getPrincipal() instanceof EliminationOauthAuthenticationImpl currentUser) {
		 var oldAuth = ((OAuth2AuthenticationToken) ctx.getAuthentication());
		 var updatedUser = userRepository.findBySubject(currentUser.getSubject()).orElseThrow();
		 var newAuth = new EliminationOauthAuthenticationImpl(updatedUser, currentUser.original());
		 var token = new OAuth2AuthenticationToken(newAuth, newAuth.getAuthorities(), oldAuth.getAuthorizedClientRegistrationId());
		 ctx.setAuthentication(token);
	  }

	  chain.doFilter(request, response);
   }

   @Autowired
   void setupRoles(RoleRepository roleRepository) {
	  if (!roleRepository.existsByName(Roles.USER.name()))
		 roleRepository.save(Roles.USER);
	  if (!roleRepository.existsByName(Roles.PLAYER.name()))
		 roleRepository.save(Roles.PLAYER);
	  if (!roleRepository.existsByName(Roles.ADMIN.name()))
		 roleRepository.save(Roles.ADMIN);

   }
}
