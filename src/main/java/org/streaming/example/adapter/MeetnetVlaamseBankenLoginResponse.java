package org.streaming.example.adapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MeetnetVlaamseBankenLoginResponse(
        String expires,
        String issued,
        @JsonProperty("access_token")
        String accessToken,
        String token_type
) {
}
