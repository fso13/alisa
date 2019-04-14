package ru.drudenko.alisa.yandex;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "first_name",
        "last_name",
        "display_name",
        "emails",
        "default_email",
        "real_name",
        "birthday",
        "openid_identities",
        "login",
        "sex",
        "id"
})
@Getter
@Setter
public class Passport {

    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("emails")
    private List<String> emails = null;
    @JsonProperty("default_email")
    private String defaultEmail;
    @JsonProperty("real_name")
    private String realName;
    @JsonProperty("birthday")
    private String birthday;
    @JsonProperty("openid_identities")
    private List<String> openidIdentities = null;
    @JsonProperty("login")
    private String login;
    @JsonProperty("sex")
    private String sex;
    @JsonProperty("id")
    private String id;
}
