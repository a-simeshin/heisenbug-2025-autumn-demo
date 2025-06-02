package ai.demo.to.qa.schema;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.ai.converter.StructuredOutputConverter;

/**
 * Конвертер позволяет создать структурированную строку в виде json через AI
 *
 * @param <T> строка на выходе
 */
@AllArgsConstructor
public class ByJsonSchema<T extends String> implements StructuredOutputConverter<T> {
    private String jsonSchema;

    @Override
    @SuppressWarnings("unchecked")
    public T convert(@NonNull final String source) {
        return (T) cleanMarkdown(source);
    }

    /**
     * Сборка основного промта для включения Structured Output
     *
     * @return часть промта с подстановкой схемы
     */
    @Override
    public String getFormat() {
        final String template = """
                Your response should be in JSON format.
                Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
                Do not include markdown code blocks in your response.
                Remove the ```json markdown from the output.
                Here is the JSON Schema instance your output must adhere to:
                ```%s```
                """;
        return String.format(template, this.jsonSchema);
    }

    /**
     * Позволяет почистить итоговый текст
     *
     * @param text итоговый текст для удаления md выделения
     * @return строка без md
     */
    public String cleanMarkdown(@NonNull String text) {
        text = text.trim();
        if (text.startsWith("```") && text.endsWith("```")) {
            final String[] lines = text.split("\n", 2);
            if (lines[0].trim().equalsIgnoreCase("```json")) {
                text = lines.length > 1 ? lines[1] : "";
            } else {
                text = text.substring(3);
            }
            text = text.substring(0, text.length() - 3);
            text = text.trim();
        }
        return text;
    }
}
