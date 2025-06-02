package ai.demo.to.qa;

import chat.giga.springai.autoconfigure.GigaChatAutoConfiguration;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.model.openai.autoconfigure.OpenAiChatAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("openrouter")
@EnableAutoConfiguration(exclude = GigaChatAutoConfiguration.class)
@SpringBootTest(classes = OpenRouterBeanGeneratorTest.CustomChatConfiguration.class)
public class OpenRouterBeanGeneratorTest {

    @Autowired
    private ChatClient chatClient;

    @Configuration
    public static class CustomChatConfiguration {

        @Bean
        public ChatClient chatModel(final ChatClient.Builder builder) {
            return builder.defaultAdvisors(new SimpleLoggerAdvisor()).build();
        }
    }

    record MyDto(
            @JsonPropertyDescription("Фамилия физ лица") String surname,
            @JsonPropertyDescription("Имя физ лица") String name,
            @JsonPropertyDescription("Отчество физ лица") String patronymic,
            @JsonPropertyDescription("Дата рождения в формате yyyy-mm-dd") String birthDate
    ) {}

    @Test
    @DisplayName("Тест генерирует dto с GigaChat")
    void generateBySchema() {
        final MyDto generated = chatClient
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
                        Твоя задача — создавать данные, соответствующие заданной схеме JSON, с учетом реальных справочников
                        РФ и форматов, используемых в государственных и корпоративных системах.
                        """)
                .call()
                .entity(MyDto.class);
        log.info("Сгенерирована dto: {}", generated);
    }
}
