package eu.kinae.k_rabbitmq_cdr.protocol.file;

import java.nio.file.Path;

import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentTargetTest;
import org.junit.jupiter.api.io.TempDir;

public abstract class FileAbstractComponentTargetTest extends AbstractComponentTargetTest {

    @TempDir
    protected static Path tempDir;

}
