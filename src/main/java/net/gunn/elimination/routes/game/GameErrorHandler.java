package net.gunn.elimination.routes.game;

import net.gunn.elimination.EliminationManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static net.gunn.elimination.auth.Roles.PLAYER;
import static net.gunn.elimination.auth.Roles.USER;

@ControllerAdvice(basePackages = "net.gunn.elimination.routes.game")
public class GameErrorHandler {
    private final EliminationManager eliminationManager;

    public GameErrorHandler(EliminationManager eliminationManager) {
        this.eliminationManager = eliminationManager;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(HttpServletResponse response) throws IOException {
        if (!eliminationManager.gameHasStarted())
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Game has not yet started");
        else if (eliminationManager.gameHasEnded())
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Game has ended");
        else if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority(USER.name()))
                && eliminationManager.gameIsOngoing()) {
            response.sendRedirect("/oauth2/authorization/google");
        } else if (!eliminationManager.gameHasEnoughPlayers())
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Game does not have enough players to have started");
        else if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority(PLAYER.name())))
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not a player");

        else
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
    }
}
