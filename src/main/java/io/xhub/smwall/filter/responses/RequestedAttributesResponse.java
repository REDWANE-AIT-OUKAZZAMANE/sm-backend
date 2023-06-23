package io.xhub.smwall.filter.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestedAttributesResponse {

    @JsonProperty("TOXICITY")
    private ToxicityResponse toxicity;


}