package academy.mischok.modules.service;

import academy.mischok.modules.model.SchoolClass;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.List;

public interface ClassService {

    List<SchoolClass> findClasses(OAuth2AuthenticationToken authentication);

}
