package net.gunn.elimination.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public static ObjectMapper getObjectMapper() {
        //The marshaller
        ObjectMapper marshaller = new ObjectMapper();

        //Make it ignore all fields unless we specify them
        marshaller.setVisibility(
            new VisibilityChecker.Std(
                JsonAutoDetect.Visibility.NONE,
                JsonAutoDetect.Visibility.NONE,
                JsonAutoDetect.Visibility.NONE,
                JsonAutoDetect.Visibility.NONE,
                JsonAutoDetect.Visibility.NONE
            )
        );

        //Allow empty objects
        marshaller.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        return marshaller;

    }
}

