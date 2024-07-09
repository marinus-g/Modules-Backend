package academy.mischok.modules.dto;

import lombok.Data;

import java.util.List;

@Data
public class AuthenticationDto {

    private final boolean authenticated;
    private final List<String> roles;

}
