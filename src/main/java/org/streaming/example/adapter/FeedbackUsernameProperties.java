package org.streaming.example.adapter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("example.feedback")
public class FeedbackUsernameProperties {

    private List<String> anonymousUsernames;

    public List<String> getAnonymousUsernames() {
        return anonymousUsernames;
    }

    public void setAnonymousUsernames(List<String> anonymousUsernames) {
        this.anonymousUsernames = anonymousUsernames;
    }
}
