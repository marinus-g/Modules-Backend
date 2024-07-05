package academy.mischok.modules.controller;

import academy.mischok.modules.dto.AuthenticationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    // the /auth/microsoft endpoint should redirect to the frontend
    @RequestMapping("/microsoft")
    public String microsoft() {
        return "redirect:https://academy-u202309-031-2febaeeb9a88.herokuapp.com/";
    }

    @GetMapping("/authenticated")
    public ResponseEntity<AuthenticationDto> authenticated(@AuthenticationPrincipal OidcUser oidcUser) {
        return ResponseEntity.ok(new AuthenticationDto(true, oidcUser.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(s -> s.startsWith("ROLE_"))
                .map(s -> s.replace("ROLE_", ""))
                .findFirst().orElse("USER")));
    }
}
