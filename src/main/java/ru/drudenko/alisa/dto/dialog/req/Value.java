package ru.drudenko.alisa.dto.dialog.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "day",
        "day_is_relative"
})
@ToString
@Getter
@Setter
public class Value {

    @JsonProperty("day")
    private Integer day;
    @JsonProperty("day_is_relative")
    private Boolean dayIsRelative;
}
