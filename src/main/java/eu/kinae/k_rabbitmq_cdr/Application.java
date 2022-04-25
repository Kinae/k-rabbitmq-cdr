package eu.kinae.k_rabbitmq_cdr;

import java.nio.file.Files;

import com.beust.jcommander.JCommander;
import eu.kinae.k_rabbitmq_cdr.connector.ConnectorFactory;
import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private Application() {
    }

    public static void main(String[] args) throws Exception {
        JCommanderParams jParams = new JCommanderParams();
        JCommander jct = JCommander.newBuilder().addObject(jParams).build();
        jct.parse(args);

        if(jParams.help) {
            jct.usage();
            return;
        }

        KOptions options = new KOptions(jParams.maxMessage);
        KParameters params = new KParameters(jParams.sourceType, jParams.sourceURI, jParams.sourceQueue, jParams.input,
                                             jParams.targetType, jParams.targetURI, jParams.targetQueue, jParams.output);

        Files.createDirectory(Constant.PROJECT_TMPDIR).toFile().deleteOnExit();
        ConnectorFactory.newConnector(params.sourceType(), params.targetType())
                .ifPresentOrElse(it -> it.start(params, options), () -> logger.error("No connector found for {} => {}", params.sourceType(), params.targetType()));
    }

}
