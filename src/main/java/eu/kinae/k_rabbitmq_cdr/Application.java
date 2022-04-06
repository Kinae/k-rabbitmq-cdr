package eu.kinae.k_rabbitmq_cdr;

import java.nio.file.Files;

import com.beust.jcommander.JCommander;
import eu.kinae.k_rabbitmq_cdr.connector.ConnectorFactory;
import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;
import eu.kinae.k_rabbitmq_cdr.utils.SourceParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private Application() {
    }

    public static void main(String[] args) throws Exception {
        JCommanderParams params = new JCommanderParams();
        JCommander jct = JCommander.newBuilder().addObject(params).build();
        jct.parse(args);

        if(params.help) {
            jct.usage();
            return;
        }

        SourceParams parameters = new SourceParams(params.maxMessage);

        Files.createDirectory(Constant.PROJECT_TMPDIR).toFile().deleteOnExit();
        ConnectorFactory.newConnector(params.sourceType, params.targetType)
                .ifPresentOrElse(it -> it.run(params), () -> logger.error("No connector found for {} => {}", params.sourceType, params.targetType));
    }

}
