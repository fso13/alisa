package ru.drudenko.alisa.api.dialog.dto.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "tokens",
        "type",
        "value"
})
@ToString
@Getter
@Setter
public class Entity {

    @JsonProperty("tokens")
    private Tokens tokens;
    @JsonProperty("type")
    private String type;
    @JsonProperty("value")
    private Object value;
}
