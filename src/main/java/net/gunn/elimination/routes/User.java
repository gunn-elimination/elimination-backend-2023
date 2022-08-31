package net.gunn.elimination.routes;

import net.gunn.elimination.auth.EliminationAuthentication;
import net.gunn.elimination.model.EliminationUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
@PreAuthorize("!isAnonymous()")
public class User {
    @GetMapping({"/", ""})
    public EliminationUser me(@AuthenticationPrincipal EliminationAuthentication user) {
        return user.user();
    }
}
