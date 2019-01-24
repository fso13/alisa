package ru.drudenko.alisa.dto.dialog.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "command",
        "original_utterance",
        "type",
        "markup",
        "nlu"
})
@ToString
@Getter
@Setter
public class Request {

    @JsonProperty("command")
    private String command;
    @JsonProperty("original_utterance")
    private String originalUtterance;
    @JsonProperty("type")
    private String type;
    @JsonProperty("markup")
    private Markup markup;
    @JsonProperty("nlu")
    private Nlu nlu;
}
