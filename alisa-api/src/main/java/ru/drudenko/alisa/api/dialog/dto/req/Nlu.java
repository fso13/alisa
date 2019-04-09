package ru.drudenko.alisa.api.dialog.dto.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "tokens",
        "entities"
})
@ToString
@Getter
@Setter
public class Nlu {

    @JsonProperty("tokens")
    private List<String> tokens = new ArrayList<>();
    @JsonProperty("entities")
    private List<Entity> entities = new ArrayList<>();
}
