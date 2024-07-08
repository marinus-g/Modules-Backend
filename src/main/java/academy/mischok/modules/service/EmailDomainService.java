package academy.mischok.modules.service;

import academy.mischok.modules.configuration.MethodSecurityConfig;
import org.springframework.stereotype.Service;

@Service
public class EmailDomainService {

    private MethodSecurityConfig methodSecurityConfig;

    public boolean endsWith(String email, String domain) {
        return email.endsWith(domain);
    }

    public String getRole(String email) {
        if (methodSecurityConfig.isParticipating(email)) {
            return "Teilnehmer";
        } else if (methodSecurityConfig.isLecturer(email)) {
            return "Dozent";
        } else {
            return "UNKNOWN";
        }
    }

    public void setMethodSecurityConfig(MethodSecurityConfig methodSecurityConfig) {
        this.methodSecurityConfig = methodSecurityConfig;
    }
}
