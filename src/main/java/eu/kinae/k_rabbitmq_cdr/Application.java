package eu.kinae.k_rabbitmq_cdr;

import java.nio.file.Files;

import com.beust.jcommander.JCommander;
import eu.kinae.k_rabbitmq_cdr.connector.ConnectorFactory;
import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.protocol.Component;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPSource;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPTarget;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;
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

        System.out.println("ts = " + Constant.TIMESTAMP);
        Files.createDirectory(Constant.PROJECT_TMPDIR).toFile().deleteOnExit();
        //        Component source = source(params.sourceType, params.sourceURI, params.sourceQueue);
        //        Component target = target(params.targetType, params.targetURI, params.targetQueue);
        ConnectorFactory.newConnector(params.sourceType, params.targetType)
                .ifPresentOrElse(it -> it.run(params), () -> logger.error("No connector found for {} => {}", params.sourceType, params.targetType));
    }

    private static Component source(SupportedType supportedType, String uri, String queue) {
        return switch(supportedType) {
            //            case FILE -> new FileSource();
            //            case AWS_S3 -> new AWSS3Source();
            case AMQP -> new AMQPSource(uri, queue);
            default -> throw new IllegalStateException("Unexpected value: " + supportedType);
        };
    }

    private static Component target(SupportedType supportedType, String uri, String queue) {
        return switch(supportedType) {
            case AMQP -> new AMQPTarget(uri, queue);
            default -> throw new IllegalStateException("Unexpected value: " + supportedType);
        };
    }

}
