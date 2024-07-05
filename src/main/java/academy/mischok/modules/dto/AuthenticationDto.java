package academy.mischok.modules.dto;

import lombok.Data;

@Data
public class AuthenticationDto {

    private final boolean authenticated;
    private final String role;

}
