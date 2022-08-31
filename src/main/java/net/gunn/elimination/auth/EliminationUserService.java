package net.gunn.elimination.auth;

import net.gunn.elimination.model.EliminationUser;
import net.gunn.elimination.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.oauth2.client.oidc.userinfo.*;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Pattern;

import static net.gunn.elimination.auth.Roles.*;
import static net.gunn.elimination.auth.Roles.PLAYER;

@Service
@Transactional
class EliminationUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {
   private final OidcUserService delegate;
   private final Set<String> adminEmails;

   private final EliminationCodeGenerator eliminationCodeGenerator;
   private final UserRepository userRepository;
   private final Instant registrationDeadline;

   @PersistenceContext
   private final EntityManager entityManager;

   public EliminationUserService(
		   EntityManager entityManager,
		   UserRepository userRepository,
		   EliminationCodeGenerator eliminationCodeGenerator,

		   @Value("${elimination.admins}") Set<String> adminEmails,
		   @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") @Value("${elimination.registration-deadline}") LocalDateTime registrationDeadline
   ) {
	  this.entityManager = entityManager;
	  this.userRepository = userRepository;
	  this.eliminationCodeGenerator = eliminationCodeGenerator;

	  this.adminEmails = adminEmails;
	  this.registrationDeadline = registrationDeadline.toInstant(ZonedDateTime.now().getOffset());

	  this.delegate = new OidcUserService();
   }

   private static final Pattern PAUSD_DOMAIN_PATTERN = Pattern.compile("[a-z]{2}[0-9]{5}@pausd\\.us");

   private boolean isValidEmail(String email) {
	  return adminEmails.contains(email) || PAUSD_DOMAIN_PATTERN.matcher(email).matches();
   }

   @Override
   public EliminationOauthAuthenticationImpl loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
	  OidcUser oidcUser = delegate.loadUser(userRequest);
	  if (!isValidEmail(oidcUser.getEmail()))
		 throw new OAuth2AuthenticationException("Only PAUSD student emails are allowed");

	  try {
		 return processOidcUser(oidcUser);
	  } catch (OAuth2AuthenticationException ex) {
		 throw ex;
	  } catch (Exception ex) {
		 throw new RuntimeException(ex);
	  }
   }

   protected void insertUserRandomly(EliminationUser user) {
	  if (userRepository.count() <= 1) {
		 user.setTarget(user);
		 user.setTargettedBy(user);
		 userRepository.save(user);
		 return;
	  }

	  var insertionPoint = (EliminationUser) entityManager.createNativeQuery("""
			  select *
			  from elimination_user u
			  WHERE subject <> :me
			    AND (select count(*)
			         from elimination_user_roles ur
			         where ur.elimination_user_subject = u.subject
			           AND ur.roles_name = :player_role) > 0
			  order by random()
			  limit 1
			  									""", EliminationUser.class)
			  .setParameter("me", user.getSubject())
			  .setParameter("player_role", PLAYER.name()).getSingleResult();

	  user.setTarget(insertionPoint.getTarget());
	  user.getTarget().setTargettedBy(user);

	  insertionPoint.setTarget(user);
	  user.setTargettedBy(insertionPoint);

	  user.addRole(PLAYER);
	  userRepository.save(user);
	  userRepository.save(user.getTarget());

	  insertionPoint.addRole(PLAYER);
	  userRepository.save(insertionPoint);
   }

   private void setupNewUser(OidcUser oidcUser) {
	  var user = new EliminationUser(
			  oidcUser.getSubject(),
			  oidcUser.getEmail(),
			  oidcUser.getGivenName(),
			  oidcUser.getFamilyName(),
			  eliminationCodeGenerator.randomCode(),
			  Set.of(USER)
	  );
	  if (userRepository.count() == 1) {
		 // Start the game
		 entityManager.createNativeQuery("""
				 insert into elimination_user_roles (elimination_user_subject, roles_name)
				 select subject, :player_role
				 from elimination_user
				 """).setParameter("player_role", PLAYER.name()).executeUpdate();
	  }

	  userRepository.save(user);
	  insertUserRandomly(user);
   }

   private EliminationOauthAuthenticationImpl processOidcUser(OidcUser oidcUser) {
	  if (!userRepository.existsBySubject(oidcUser.getSubject())) {
		 if (Instant.now().isAfter(registrationDeadline))
			throw new OAuth2AuthenticationException("Registration is closed");

		 setupNewUser(oidcUser);
	  }

	  var user = userRepository.findBySubject(oidcUser.getSubject()).orElseThrow();
	  if (adminEmails.contains(user.getEmail()) && !user.getRoles().contains(ADMIN)) {
		 user.addRole(ADMIN);
		 user = userRepository.save(user);
	  }

	  return new EliminationOauthAuthenticationImpl(user, oidcUser);
   }
}