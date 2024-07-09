package academy.mischok.modules;

import academy.mischok.modules.configuration.OAuth2ClientConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(OAuth2ClientConfiguration.class)
class ModulesBackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
