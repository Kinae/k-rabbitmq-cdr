package eu.kinae.k_rabbitmq_cdr.component.file;

import java.nio.file.Path;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentTargetTest;
import org.junit.jupiter.api.io.TempDir;

public abstract class FileAbstractComponentTargetTest extends AbstractComponentTargetTest {

    @TempDir
    protected Path tempDir;

}
