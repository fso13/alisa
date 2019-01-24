package ru.drudenko.alisa.dto.dialog;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "new",
        "message_id",
        "session_id",
        "skill_id",
        "user_id"
})
@ToString
@Getter
@Setter
public class Session {

    @JsonProperty("new")
    private Boolean _new;
    @JsonProperty("message_id")
    private Integer messageId;
    @JsonProperty("session_id")
    private String sessionId;
    @JsonProperty("skill_id")
    private String skillId;
    @JsonProperty("user_id")
    private String userId;
}
