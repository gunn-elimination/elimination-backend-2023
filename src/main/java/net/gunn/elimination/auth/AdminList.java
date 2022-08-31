package net.gunn.elimination.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Set;

@ConfigurationProperties(prefix = "elimination")
@ConstructorBinding
record AdminList(Set<String> admins) {
    boolean isAdmin(String email) {
        return admins.contains(email);
    }
}
