package eu.kinae.k_rabbitmq_cdr.component.file;

import java.nio.file.Path;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentSourceTest;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public abstract class FileAbstractComponentSourceTest extends AbstractComponentSourceTest {

    @TempDir
    protected Path tempDir;

    @TempDir
    protected Path tempEmptyDir;

    @BeforeEach
    public void beforeEach() throws Exception {
        var writer = new FileWriter(tempDir);
        for(var message : MESSAGES) {
            writer.push(message);
        }
    }

    @Override
    protected FileReader getEmptySource() {
        return new FileReader(new FileReaderInfo(tempEmptyDir, KOptions.DEFAULT));
    }

    @Override
    protected FileReader getSource(KOptions options) {
        return new FileReader(new FileReaderInfo(tempDir, options));
    }

    protected SharedQueue getMockTarget() {
        return mock(SharedQueue.class);
    }

    protected abstract SharedQueue getSharedQueue();

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        var options = KOptions.DEFAULT;
        var target = getSharedQueue();
        try(var component = getComponent(getSource(options), target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            assertThatContainsAllMessages(target, options);
        }
    }

}
