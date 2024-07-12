package academy.mischok.modules.service;

import academy.mischok.modules.exception.AuthorizationException;
import academy.mischok.modules.exception.ModuleAlreadyPresentException;
import academy.mischok.modules.exception.ModuleNotFoundException;
import academy.mischok.modules.exception.SchoolClassNotFoundException;
import academy.mischok.modules.model.ClassModule;
import academy.mischok.modules.model.SchoolClass;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClassService {

    List<SchoolClass> findClasses();

    Optional<SchoolClass> findClassByName(String name);

    Optional<SchoolClass> findClassByUser(OAuth2AuthenticationToken token);

    void addModuleToClass(UUID classId, Long moduleId) throws SchoolClassNotFoundException, ModuleNotFoundException, ModuleAlreadyPresentException;

    List<ClassModule> findClassModules(OAuth2AuthenticationToken token, UUID classId) throws SchoolClassNotFoundException, AuthorizationException;

    Optional<ClassModule> findClassModule(OAuth2AuthenticationToken token, UUID classId, Long moduleId) throws SchoolClassNotFoundException, AuthorizationException;

    void removeModuleFromClass(UUID classId, Long moduleId) throws SchoolClassNotFoundException, ModuleNotFoundException;

    Optional<SchoolClass> findClassById(OAuth2AuthenticationToken token, UUID classId) throws AuthorizationException;

    void save(ClassModule classModule);
}
