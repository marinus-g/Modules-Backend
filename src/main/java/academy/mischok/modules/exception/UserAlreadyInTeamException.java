package academy.mischok.modules.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserAlreadyInTeamException extends Exception {

    private final UUID userId;

    public UserAlreadyInTeamException(UUID userId) {
        super(String.format("User with id %s is already in the team", userId));
        this.userId = userId;
    }
}
