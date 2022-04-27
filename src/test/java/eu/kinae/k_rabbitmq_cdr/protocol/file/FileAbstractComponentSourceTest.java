package eu.kinae.k_rabbitmq_cdr.protocol.file;

import java.nio.file.Path;

import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentSourceTest;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public abstract class FileAbstractComponentSourceTest extends AbstractComponentSourceTest {

    @TempDir
    protected static Path tempDir;

    @TempDir
    protected static Path tempEmptyDir;

    @BeforeAll
    public static void beforeAll() throws Exception {
        for(KMessage message : MESSAGES) {
            new FileWriter(tempDir).push(message);
        }
    }

    @Override
    protected FileReader getEmptySource() {
        return new FileReader(tempEmptyDir);
    }

    @Override
    protected FileReader getSource() {
        return new FileReader(tempDir);
    }

    protected SharedQueue getMockTarget() {
        return mock(SharedQueue.class);
    }

    protected abstract SharedQueue getSharedQueue();

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        try(var target = getSharedQueue();
            var component = getComponent(getSource(), target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            for(KMessage message : MESSAGES) {
                assertThat(target.pop()).isEqualTo(message);
            }
        }
    }

}
