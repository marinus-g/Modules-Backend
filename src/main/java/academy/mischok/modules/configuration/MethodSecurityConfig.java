package academy.mischok.modules.configuration;

import academy.mischok.modules.service.EmailDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration("methodSecurity")
@EnableMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig {

    private final EmailDomainService emailDomainService;

    @Autowired
    public MethodSecurityConfig(EmailDomainService emailDomainService) {
        this.emailDomainService = emailDomainService;
        emailDomainService.setMethodSecurityConfig(this);
    }

    public boolean isParticipating(String email) {
        return emailDomainService.endsWith(email, "@tn.mischok.academy");
    }

    public boolean isLecturer(String email) {
        return emailDomainService.endsWith(email, "@dozent.mischok.academy");
    }

}
