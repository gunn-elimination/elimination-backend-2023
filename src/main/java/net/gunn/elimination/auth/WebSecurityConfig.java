package net.gunn.elimination.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
class WebSecurityConfig {
    private final EliminationUserService userDetailsService;

    public WebSecurityConfig(EliminationUserService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    protected SecurityFilterChain webSecurityCustomizer(HttpSecurity http) throws Exception {
        return http.authorizeRequests().anyRequest().permitAll().and().oauth2Login().userInfoEndpoint(n -> n.oidcUserService(userDetailsService)).and().build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(
        @Value("${spring.security.oauth2.client.registration.google.clientId}") String clientId,
        @Value("${spring.security.oauth2.client.registration.google.clientSecret}") String clientSecret
    ) {
        return new InMemoryClientRegistrationRepository(
            CommonOAuth2Provider
                .GOOGLE
                .getBuilder("google")
                .redirectUri("{baseUrl}/login/oauth2/code/google")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build()
        );
    }

  /* @Configuration
   static class RoleReloaderConfig {
	  private final RoleReloader roleReloader;

	  RoleReloaderConfig(RoleReloader roleReloader) {
		 this.roleReloader = roleReloader;
	  }

	  @Autowired
	  protected void installRoleReloader(@Autowired HttpSecurity http) {
		 http.addFilterAfter(roleReloader, SwitchUserFilter.class);
	  }
   }*/
}
