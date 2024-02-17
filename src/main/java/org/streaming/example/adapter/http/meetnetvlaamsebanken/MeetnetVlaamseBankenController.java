package org.streaming.example.adapter.http.meetnetvlaamsebanken;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.streaming.example.adapter.MeetnetVlaamseBankenCatalog;
import org.streaming.example.adapter.MeetnetVlaamseBankenData;
import org.streaming.example.adapter.MeetnetVlaamseBankenLoginResponse;
import org.streaming.example.domain.meetnetvlaamsebanken.MeetnetVlaamseBankenProperties;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Service
@EnableScheduling
public class MeetnetVlaamseBankenController {

    private final MeetnetVlaamseBankenProperties meetnetVlaamseBankenProperties;
    private final RestTemplate restTemplate;

    public MeetnetVlaamseBankenController(
            MeetnetVlaamseBankenProperties meetnetVlaamseBankenProperties,
            RestTemplate restTemplate) {
        this.meetnetVlaamseBankenProperties = meetnetVlaamseBankenProperties;
        this.restTemplate = restTemplate;
    }

    // TODO - method that call findData and findCatalogData and merge them together. Create event per location

    public String token() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);
        String body = String.format("grant_type=password&username=%s&password=%s", meetnetVlaamseBankenProperties.getUsername(), meetnetVlaamseBankenProperties.getPassword());

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

        var forObject = Optional.ofNullable(restTemplate.exchange("%s/Token?grant_type=password".formatted(meetnetVlaamseBankenProperties.getBaseUrl()), POST, requestEntity, MeetnetVlaamseBankenLoginResponse.class).getBody());
        return forObject.map(MeetnetVlaamseBankenLoginResponse::accessToken).orElse(null);
    }

    public MeetnetVlaamseBankenCatalog findCatalogData() {
        return restTemplate.exchange(
                        "%s/V2/catalog".formatted(meetnetVlaamseBankenProperties.getBaseUrl()),
                        HttpMethod.GET,
                        getHttpEntity(),
                        MeetnetVlaamseBankenCatalog.class).getBody();
    }


    public List<MeetnetVlaamseBankenData> findData() {
        var responseType = new ParameterizedTypeReference<List<MeetnetVlaamseBankenData>>() {};

        return restTemplate.exchange(
                        "%s/V2/currentData".formatted(meetnetVlaamseBankenProperties.getBaseUrl()),
                        HttpMethod.GET,
                        getHttpEntity(),
                        responseType).getBody();
    }

    private HttpEntity<String> getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(meetnetVlaamseBankenProperties.getUsername(), meetnetVlaamseBankenProperties.getPassword());
        headers.set("Authorization", "Bearer " + token());
       return new HttpEntity<>(headers);
    }

}
