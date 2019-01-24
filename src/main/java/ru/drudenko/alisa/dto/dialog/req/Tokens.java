package ru.drudenko.alisa.dto.dialog.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "start",
        "end"
})
@ToString
@Getter
@Setter
public class Tokens {

    @JsonProperty("start")
    private Integer start;
    @JsonProperty("end")
    private Integer end;

}
