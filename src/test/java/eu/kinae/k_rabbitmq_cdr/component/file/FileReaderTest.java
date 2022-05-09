package eu.kinae.k_rabbitmq_cdr.component.file;

import java.nio.file.Path;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentTest;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileReaderTest extends AbstractComponentTest {

    @TempDir
    protected Path tempDir;

    @Test
    public void Read_messages_are_equal_to_original() throws Exception {
        var target = new FileWriter(tempDir);
        for(var message : MESSAGES) {
            target.push(message);
        }

        var reader = new FileReader(tempDir, KOptions.DEFAULT);

        assertThatSourceContainsAllMessagesUnsorted(reader);
    }
}
