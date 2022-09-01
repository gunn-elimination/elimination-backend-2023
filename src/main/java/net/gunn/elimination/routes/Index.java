package net.gunn.elimination.routes;

import net.gunn.elimination.auth.EliminationAuthentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@Controller
public class Index {
    @RequestMapping("/")
    public String index(@AuthenticationPrincipal EliminationAuthentication user) {
        if (user == null)
            return "redirect:/login";
        return "index";
    }
}
