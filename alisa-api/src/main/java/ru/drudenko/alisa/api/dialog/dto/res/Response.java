package ru.drudenko.alisa.api.dialog.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "text",
        "tts",
        "end_session"
})
@ToString
@Getter
@Setter
public class Response {

    @JsonProperty("text")
    private String text;
    @JsonProperty("tts")
    private String tts;
    @JsonProperty("end_session")
    private Boolean endSession;

}
