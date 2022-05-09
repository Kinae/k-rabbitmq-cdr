package eu.kinae.k_rabbitmq_cdr.component.file;

import java.nio.file.Path;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentTest;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class FileWriterTest extends AbstractComponentTest {

    @TempDir
    protected Path tempDir;

    @Test
    public void Pushed_messages_are_equal_to_original() throws Exception {
        var writer = new FileWriter(tempDir);
        for(var message : MESSAGES) {
            writer.push(message);
        }

        var reader = new FileReader(tempDir, KOptions.DEFAULT);
        assertThatSourceContainsAllMessagesUnsorted(reader);
    }
}
