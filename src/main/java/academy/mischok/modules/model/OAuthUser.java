package academy.mischok.modules.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OAuthUser {

    @JsonProperty("id")
    String id;
    @JsonProperty("givenName")
    String firstName;
    @JsonProperty("surname")
    String lastName;
    @JsonProperty("mail")
    String mail;
}
