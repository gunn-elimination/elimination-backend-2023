package net.gunn.elimination.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
class WebSecurityConfig {
    private final EliminationUserService userDetailsService;

    public WebSecurityConfig(EliminationUserService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    @Autowired
    protected SecurityFilterChain webSecurityCustomizer(
        HttpSecurity http
        ) throws Exception {
        return http
            .authorizeRequests()
            .anyRequest()
            .permitAll()
            .and()
            .oauth2Login(n -> n.userInfoEndpoint().oidcUserService(userDetailsService))
            .headers().frameOptions().sameOrigin()
            .and()
            .build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(@Value("classpath:client_secret.json") Resource clientSecret, ObjectMapper mapper) throws IOException {
        var config = mapper.readTree(clientSecret.getInputStream()).get("web");
        return new InMemoryClientRegistrationRepository(

            CommonOAuth2Provider
                .GOOGLE
                .getBuilder("google")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/google")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .clientId(config.get("client_id").asText())
                .clientSecret(config.get("client_secret").asText())
                .build()
        );
    }

}
