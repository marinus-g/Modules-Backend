package academy.mischok.modules.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/hello")
public class HelloWorldController {

    @GetMapping
    public ResponseEntity<List<String>> test(@AuthenticationPrincipal OidcUser user) {
        System.out.println("Hello World " + user.getClaimAsString("roles"));
        return ResponseEntity.ok(List.of("Hello World", user.getFullName(), user.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(", "))));
    }

}
