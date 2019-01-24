package ru.drudenko.alisa.dto.dialog.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "locale",
        "timezone",
        "client_id"
})
@ToString
@Getter
@Setter
public class Meta {

    @JsonProperty("locale")
    private String locale;
    @JsonProperty("timezone")
    private String timezone;
    @JsonProperty("client_id")
    private String clientId;
}
