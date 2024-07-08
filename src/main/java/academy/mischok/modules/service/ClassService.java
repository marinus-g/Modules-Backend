package academy.mischok.modules.service;

import academy.mischok.modules.model.SchoolClass;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.List;
import java.util.Optional;

public interface ClassService {

    List<SchoolClass> findClasses(OAuth2AuthenticationToken authentication);

    Optional<SchoolClass> findClassByName(String name);
}
