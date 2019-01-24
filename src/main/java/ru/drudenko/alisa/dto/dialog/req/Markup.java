package ru.drudenko.alisa.dto.dialog.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "dangerous_context"
})
@ToString
@Getter
@Setter
public class Markup {

    @JsonProperty("dangerous_context")
    private Boolean dangerousContext;
}
