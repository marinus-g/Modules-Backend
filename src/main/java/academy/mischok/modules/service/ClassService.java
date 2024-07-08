package academy.mischok.modules.service;

import academy.mischok.modules.model.SchoolClass;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;

public interface ClassService {

    List<SchoolClass> findClasses();

    Optional<SchoolClass> findClassByName(String name);

    Optional<SchoolClass> findClassByUser(OAuth2AuthenticationToken token);
}
