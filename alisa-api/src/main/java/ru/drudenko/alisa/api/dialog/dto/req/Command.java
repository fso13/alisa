package ru.drudenko.alisa.api.dialog.dto.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.drudenko.alisa.api.dialog.dto.Session;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "meta",
        "request",
        "session",
        "version"
})
@ToString
@Getter
@Setter
public class Command {

    @JsonProperty("meta")
    private Meta meta;
    @JsonProperty("request")
    private Request request;
    @JsonProperty("session")
    private Session session;
    @JsonProperty("version")
    private String version;
}
