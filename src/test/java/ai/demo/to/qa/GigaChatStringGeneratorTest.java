package ai.demo.to.qa;

import ai.demo.to.qa.configuration.JsonSchemaStructuredOutputConfiguration;
import ai.demo.to.qa.schema.ByJsonSchema;
import com.networknt.schema.InputFormat;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EnableAutoConfiguration
@ActiveProfiles("gigachat")
@SpringBootTest(classes = JsonSchemaStructuredOutputConfiguration.class)
public class GigaChatStringGeneratorTest {

    @Autowired
    private ByJsonSchema<String> byJsonSchema;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private JsonSchema jsonSchema;

    @Test
    @DisplayName("Тест генерирует строковые данные с GigaChat")
    void generateBySchema() {
        final String generated = chatClient
                .prompt(
                        """
                                Данные должны:
                                Соответствовать российским нормам именования (ФИО с отчеством)
                                Учитывать возрастные диапазоны, актуальные для справочников РФ (например, возраст от
                                18 до 75 лет)
                                """
                )
                .system("""
                        Ты — генератор тестовых данных, ориентированный на соответствие российским стандартам и кодификаторам.
                        Твоя задача — создавать данные, соответствующие заданной схеме JSON, с учетом реальных справочников РФ и форматов,
                        используемых в государственных и корпоративных системах.
                        """)
                .call()
                .entity(byJsonSchema);
        log.info(generated);

        final Set<ValidationMessage> validationErrors = jsonSchema.validate(generated, InputFormat.JSON);
        log.info("Ошибки валидации по схеме: {}", validationErrors.toString());

        assertTrue(validationErrors.isEmpty(), "Валидация по схеме сгенерированных данных пройдена");
    }
}
