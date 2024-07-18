package academy.mischok.modules.service.impl;

import academy.mischok.modules.exception.AuthorizationException;
import academy.mischok.modules.exception.ModuleAlreadyPresentException;
import academy.mischok.modules.exception.ModuleNotFoundException;
import academy.mischok.modules.exception.SchoolClassNotFoundException;
import academy.mischok.modules.model.ClassModule;
import academy.mischok.modules.model.Module;
import academy.mischok.modules.model.OAuthUser;
import academy.mischok.modules.model.SchoolClass;
import academy.mischok.modules.repository.ClassModuleRepository;
import academy.mischok.modules.repository.ModuleRepository;
import academy.mischok.modules.service.ClassService;
import academy.mischok.modules.service.ModuleService;
import academy.mischok.modules.service.OauthClientService;
import academy.mischok.modules.service.impl.cache.ClassServiceCache;
import academy.mischok.modules.service.impl.cache.UserServiceCache;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("DuplicatedCode")
@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {


    private static final List<SchoolClass> EMPTY_LIST = List.of();
    private static final Logger log = LoggerFactory.getLogger(ClassServiceImpl.class);
    private final ClassServiceCache classServiceCache;
    private final ModuleRepository moduleRepository;
    private final UserServiceCache userServiceCache;

    @Value("${microsoft.graph-url}")
    private String graphUrl;


    private final OauthClientService oAuthClientService;
    private final RestTemplate restTemplate;
    private final ModuleService moduleService;
    private final ClassModuleRepository classModuleRepository;

    @SneakyThrows
    public List<SchoolClass> findClasses() {
        return fetchClasses();
    }

    @Override
    public Optional<SchoolClass> findClassByName(String name) {
        return fetchClasses()
                .stream()
                .filter(schoolClass -> schoolClass.getName().equals(name))
                .findFirst();
    }

    @Override
    public Optional<SchoolClass> findClassByUser(OAuth2AuthenticationToken token) {
        fetchClasses();
        return token
                .getAuthorities()
                .stream()
                .filter(grantedAuthority -> grantedAuthority.getAuthority().startsWith("ROLE_"))
                .map(grantedAuthority -> grantedAuthority.getAuthority().substring(5))
                .filter(name -> name.startsWith("U") && name.endsWith("UFI"))
                .findFirst()
                .flatMap(this::findClassByName);

    }

    @Override
    public void addModuleToClass(UUID classId, Long moduleId) throws SchoolClassNotFoundException, ModuleNotFoundException, ModuleAlreadyPresentException {
        SchoolClass schoolClass = findById(classId)
                .orElseThrow(() -> new SchoolClassNotFoundException(classId));
        Module module = moduleService.findModuleById(moduleId)
                .orElseThrow(() -> new ModuleNotFoundException(moduleId));
        if (classModuleRepository.findBySchoolClassAndModule_Id(classId, moduleId).isPresent()) {
            throw new ModuleAlreadyPresentException(moduleId, module.getName());
        }
        ClassModule classModule = ClassModule
                .builder()
                .schoolClass(schoolClass.getClassId())
                .module(module)
                .startDate(new java.sql.Timestamp(System.currentTimeMillis()))
                .build();
        classModuleRepository.save(classModule);
    }

    @Override
    public List<ClassModule> findClassModules(OAuth2AuthenticationToken token, UUID classId) throws SchoolClassNotFoundException, AuthorizationException {
        fetchClasses();
        final SchoolClass schoolClass = findById(classId)
                .orElseThrow(() -> new SchoolClassNotFoundException(classId));
        final String className = schoolClass.getName();
        if (token.getAuthorities().stream().noneMatch(grantedAuthority
                -> grantedAuthority.getAuthority().equals("ROLE_" + className) || grantedAuthority.getAuthority().equals("ROLE_Dozentenkollegium"))) {
            throw new AuthorizationException("Not authorized to access this class");
        }
        return classModuleRepository.findBySchoolClass(classId);
    }

    @Override
    public Optional<ClassModule> findClassModule(OAuth2AuthenticationToken token, UUID classId, Long moduleId) throws SchoolClassNotFoundException, AuthorizationException {
        fetchClasses();
        final SchoolClass schoolClass = findClassById(token, classId)
                .orElseThrow(() -> new SchoolClassNotFoundException(classId));
        final String className = schoolClass.getName();

        if (token.getAuthorities().stream().noneMatch(grantedAuthority
                -> grantedAuthority.getAuthority().equals("ROLE_" + className) || grantedAuthority.getAuthority().equals("ROLE_Dozentenkollegium"))) {
            throw new AuthorizationException("Not authorized to access this class");
        }
        return classModuleRepository.findBySchoolClassAndModule_Id(classId, moduleId);
    }

    @Override
    public Optional<SchoolClass> findClassById(OAuth2AuthenticationToken token, UUID classId) throws AuthorizationException {
        final Optional<SchoolClass> schoolClass = findById(classId);
        if (schoolClass.isEmpty()) {
            return schoolClass;
        }
        final String className = schoolClass.get().getName();
        if (token.getAuthorities().stream().noneMatch(grantedAuthority
                -> grantedAuthority.getAuthority().equals("ROLE_" + className) || grantedAuthority.getAuthority().equals("ROLE_Dozentenkollegium"))) {
            throw new AuthorizationException("Not authorized to access this class");
        }
        return schoolClass;
    }

    @Override
    public void save(ClassModule classModule) {
        this.classModuleRepository.save(classModule);
    }

    @Override
    public List<OAuthUser> findUsersInClass(UUID classId) {
        return this.userServiceCache.findUsersInGroup(classId);
    }

    private Optional<SchoolClass> findById(UUID classId) {
        return this.fetchClasses()
                .stream()
                .filter(schoolClass -> schoolClass.getClassId().equals(classId))
                .findFirst();
    }

    @Override
    public void removeModuleFromClass(UUID classId, Long moduleId) throws SchoolClassNotFoundException, ModuleNotFoundException {
        final ClassModule classModule = classModuleRepository.findBySchoolClassAndModule_Id(classId, moduleId)
                .orElseThrow(() -> new ModuleNotFoundException(String.format("Module with id %d not found in class %s", moduleId, classId)));
        classModuleRepository.delete(classModule);
    }

    protected synchronized List<SchoolClass> fetchClasses() {
        return classServiceCache.fetchClasses();
    }
}