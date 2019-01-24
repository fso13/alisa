package ru.drudenko.alisa.dto.dialog.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.drudenko.alisa.dto.dialog.Session;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "response",
        "session",
        "version"
})
@ToString
@Getter
@Setter
public class Render {

    @JsonProperty("response")
    private Response response = new Response();
    @JsonProperty("session")
    private Session session;
    @JsonProperty("version")
    private String version = "1.0";
}
