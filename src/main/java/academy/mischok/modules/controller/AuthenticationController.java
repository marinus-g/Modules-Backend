package academy.mischok.modules.controller;

import academy.mischok.modules.dto.AuthenticationDto;
import org.springframework.beans.factory.annotation.Value;
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

    private final String redirectUrl;

    public AuthenticationController(@Value("microsoft.redirect-uri") String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @RequestMapping("/microsoft")
    public String microsoft() {
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/authenticated")
    public ResponseEntity<AuthenticationDto> authenticated(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null)
            return ResponseEntity.ok(new AuthenticationDto(false, null));
        return ResponseEntity.ok(new AuthenticationDto(true, oidcUser.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(s -> s.startsWith("ROLE_"))
                .map(s -> s.replace("ROLE_", ""))
                .toList()));
    }
}
