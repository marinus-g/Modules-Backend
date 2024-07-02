package academy.mischok.modules.controller;

import com.azure.core.annotation.Get;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloWorldController {

    @GetMapping(produces = "application/json")
    public ResponseEntity<Temp> helloWorld(@AuthenticationPrincipal OidcUser user) {
        return ResponseEntity
                .ok(new Temp("Hello, " + user.getFullName() + "! Du bist " + user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                        .filter(authority -> authority.startsWith("ROLE_"))
                        .findFirst().orElse("ROLE_UNDEFINED").substring(5).toLowerCase()));
    }

}


