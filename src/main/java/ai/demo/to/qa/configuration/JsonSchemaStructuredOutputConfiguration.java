package ai.demo.to.qa.configuration;

import ai.demo.to.qa.schema.ByJsonSchema;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;

@Configuration
public class JsonSchemaStructuredOutputConfiguration {

    @Bean
    public ChatClient chatModel(final ChatClient.Builder builder) {
        return builder.defaultAdvisors(new SimpleLoggerAdvisor()).build();
    }

    @Bean
    public ByJsonSchema<String> byJsonSchema() {
        return new ByJsonSchema<>(jsonSchemaString());
    }

    @Bean
    public JsonSchema jsonSchema() {
        return JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012).getSchema(jsonSchemaString());
    }

    @SneakyThrows
    String jsonSchemaString() {
        return Files.readString(new ClassPathResource("jsonschema.json").getFile().toPath());
    }
}
