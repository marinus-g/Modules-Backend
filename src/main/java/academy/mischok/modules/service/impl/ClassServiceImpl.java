package academy.mischok.modules.service.impl;

import academy.mischok.modules.exception.AuthorizationException;
import academy.mischok.modules.exception.ModuleAlreadyPresentException;
import academy.mischok.modules.exception.ModuleNotFoundException;
import academy.mischok.modules.exception.SchoolClassNotFoundException;
import academy.mischok.modules.model.ClassModule;
import academy.mischok.modules.model.Module;
import academy.mischok.modules.model.SchoolClass;
import academy.mischok.modules.repository.ClassModuleRepository;
import academy.mischok.modules.repository.SchoolClassRepository;
import academy.mischok.modules.service.ClassService;
import academy.mischok.modules.service.ModuleService;
import academy.mischok.modules.service.OauthClientService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("DuplicatedCode")
@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {


    private static final List<SchoolClass> EMPTY_LIST = List.of();
    private static final String GROUPS_ENDPOINT = "/v1.0/groups";
    private static final Logger log = LoggerFactory.getLogger(ClassServiceImpl.class);

    @Value("${microsoft.graph-url}")
    private String graphUrl;


    private final OauthClientService oAuthClientService;
    private final RestTemplate restTemplate;
    private final SchoolClassRepository repository;
    private final ModuleService moduleService;
    private final ClassModuleRepository classModuleRepository;
    private long lastFetch = 0;

    @SneakyThrows public List<SchoolClass> findClasses() {


        List<SchoolClass> all = Optional.ofNullable(fetchClasses())
                .filter(schoolClasses -> !schoolClasses.isEmpty())
                .orElseGet(() -> {
                    log.info("fetching classes from repository at: {}", System.currentTimeMillis());
                    return repository.findAll();
                });
        log.info("Found {} classes", all.size());
        log.debug("!!Classes: {} at {}", all, System.currentTimeMillis());
        return all;
    }

    @Override
    public Optional<SchoolClass> findClassByName(String name) {
        fetchClasses();
        return repository.findByClassId(name);
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
                .flatMap(repository::findByName);

    }

    @Override
    public void addModuleToClass(Long classId, Long moduleId) throws SchoolClassNotFoundException, ModuleNotFoundException, ModuleAlreadyPresentException {
        SchoolClass schoolClass = repository.findById(classId)
                .orElseThrow(() -> new SchoolClassNotFoundException(classId));

        Module module = moduleService.findModuleById(moduleId)
                .orElseThrow(() -> new ModuleNotFoundException(moduleId));
        if (schoolClass.getModules().stream().anyMatch(classModule -> classModule.getModule().getId().equals(moduleId))) {
            throw new ModuleAlreadyPresentException(moduleId, module.getName());
        }
        ClassModule classModule = ClassModule
                .builder()
                .schoolClass(schoolClass)
                .module(module)
                .startDate(new java.sql.Timestamp(System.currentTimeMillis()))
                .build();
        classModule = classModuleRepository.save(classModule);
        schoolClass.getModules().add(classModule);
        repository.save(schoolClass);
    }

    @Override
    public List<ClassModule> findClassModules(OAuth2AuthenticationToken token, Long classId) throws SchoolClassNotFoundException, AuthorizationException {
        fetchClasses();
        final SchoolClass schoolClass = repository.findById(classId)
                .orElseThrow(() -> new SchoolClassNotFoundException(classId));
        final String className = schoolClass.getName();
        if (token.getAuthorities().stream().noneMatch(grantedAuthority
                -> grantedAuthority.getAuthority().equals("ROLE_" + className) || grantedAuthority.getAuthority().equals("ROLE_Dozentenkollegium"))) {
            throw new AuthorizationException("Not authorized to access this class");
        }
        return schoolClass.getModules();
    }

    @Override
    public Optional<ClassModule> findClassModule(OAuth2AuthenticationToken token, Long classId, Long moduleId) throws SchoolClassNotFoundException, AuthorizationException {
        fetchClasses();
        final SchoolClass schoolClass = repository.findById(classId)
                .orElseThrow(() -> new SchoolClassNotFoundException(classId));
        final String className = schoolClass.getName();

        if (token.getAuthorities().stream().noneMatch(grantedAuthority
                -> grantedAuthority.getAuthority().equals("ROLE_" + className) || grantedAuthority.getAuthority().equals("ROLE_Dozentenkollegium"))) {
            throw new AuthorizationException("Not authorized to access this class");
        }
        return schoolClass.getModules().stream()
                .filter(classModule -> classModule.getModule().getId().equals(moduleId))
                .findFirst();

    }

    @Override
    public Optional<SchoolClass> findClassById(OAuth2AuthenticationToken token, Long classId) throws AuthorizationException {
        fetchClasses();
        final Optional<SchoolClass> schoolClass = repository.findById(classId);
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
    public void removeModuleFromClass(Long classId, Long moduleId) throws SchoolClassNotFoundException, ModuleNotFoundException {
        SchoolClass schoolClass = repository.findById(classId)
                .orElseThrow(() -> new SchoolClassNotFoundException(classId));
        final ClassModule classModule = schoolClass.getModules().stream()
                .filter(module -> module.getModule().getId().equals(moduleId))
                .findFirst()
                .orElseThrow(() -> new ModuleNotFoundException(String.format("Module with id %d not found in class %d", moduleId, classId)));
        schoolClass.getModules().remove(classModule);
        repository.save(schoolClass);
        classModuleRepository.delete(classModule);
    }

    protected synchronized List<SchoolClass> fetchClasses() {
        if (lastFetch + 1000 * 60 * 60 > System.currentTimeMillis()) {
             return EMPTY_LIST;
        }
        HttpEntity<String> entity = OAuthClientServiceImpl.buildHttpEntity(oAuthClientService);
        ResponseEntity<String> response = restTemplate.exchange(graphUrl + GROUPS_ENDPOINT, HttpMethod.GET, entity, String.class);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new IllegalStateException("Failed to fetch classes");
        }
        JsonObject object = JsonParser.parseString(response.getBody()).getAsJsonObject();
        lastFetch = System.currentTimeMillis();

        return object.get("value").getAsJsonArray().asList().stream()
                .map(JsonElement::getAsJsonObject)
                .map(jsonObject
                        -> new SchoolClass(
                        jsonObject.get("id").getAsString(),
                        jsonObject.get("displayName").getAsString())
                )
                .filter(schoolClass -> schoolClass.getName().startsWith("U") && schoolClass.getName().endsWith("UFI"))
                .map(schoolClass ->
                        repository.findByClassId(schoolClass.getClassId())
                                .map(schoolClass1 -> {
                                    schoolClass1.setName(schoolClass.getName());
                                    schoolClass1 = repository.saveAndFlush(schoolClass1);
                                    return schoolClass1;
                                })
                                .orElseGet(() -> {
                                    log.info("Creating new class: {}", schoolClass);
                                    schoolClass.setModules(new ArrayList<>());
                                    SchoolClass schoolClass1 =  repository.saveAndFlush(schoolClass);
                                    return schoolClass1;
                                }))
                .toList();
    }
}