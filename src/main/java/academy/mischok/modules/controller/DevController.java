package academy.mischok.modules.controller;

import academy.mischok.modules.configuration.WebSecurityConfiguration;
import academy.mischok.modules.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController("/dev")
@RequestMapping("/dev")
@RequiredArgsConstructor
public class DevController {


    private final UserService userService;

    @PostMapping("/toggle")
    public ResponseEntity<Void> lecturer(OAuth2AuthenticationToken token, HttpServletRequest request) {
        System.out.println("EMAIL::: " + token.getPrincipal().getAttribute("email"));
        if (WebSecurityConfiguration.LECTURER_EMAILS.contains(Objects.requireNonNull(token.getPrincipal().getAttribute("email")).toString().toLowerCase())) {
            WebSecurityConfiguration.LECTURER_EMAILS.remove(token.getPrincipal().getAttribute("email").toString().toLowerCase());
        } else {
            WebSecurityConfiguration.LECTURER_EMAILS.add(token.getPrincipal().getAttribute("email").toString().toLowerCase());
        }
        System.out.println("EMAILS::: " + WebSecurityConfiguration.LECTURER_EMAILS);
        // the user should get logged out
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();

        return ResponseEntity.ok().build();
    }
}
