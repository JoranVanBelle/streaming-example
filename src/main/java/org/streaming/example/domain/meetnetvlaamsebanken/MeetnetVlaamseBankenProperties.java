package org.streaming.example.domain.meetnetvlaamsebanken;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("example.http.meetnet-vlaamse-banken")
public final class MeetnetVlaamseBankenProperties {

    /**
     * The base url used to fetch the data from Meetnet Vlaamse banken
     */
    private String baseUrl;

    /**
     * The username used to login to Meetnet Vlaamse Banken
     */
    private String username;

    /**
     * The password used to login to Meetnet Vlaamse Banken
     */
    private String password;

    /**
     * The list of sensors used to collect data from.<br/>
     * In case of kiteable weather, you need to have 3 types of sensors:<br/>
     *      - Wind speed<br/>
     *      - Wave height<br/>
     *      - Wind direction<br/>
     */
    private List<String> sensorIds;

    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
