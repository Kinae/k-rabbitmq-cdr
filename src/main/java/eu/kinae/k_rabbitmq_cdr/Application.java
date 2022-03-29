package eu.kinae.k_rabbitmq_cdr;

import java.nio.file.Files;

import com.beust.jcommander.JCommander;
import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPSource;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPTarget;
import eu.kinae.k_rabbitmq_cdr.protocol.aws.AWSS3Source;
import eu.kinae.k_rabbitmq_cdr.protocol.file.FileSource;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;

public final class Application {

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
        Source source = source(params.sourceType, params.sourceURI, params.sourceQueue);
        source.run();

        Target target = target(params.targetType, params.targetURI, params.targetQueue);
        target.run();
    }

    private static Source source(SupportedType supportedType, String uri, String queue) {
        return switch(supportedType) {
            case FILE -> new FileSource();
            case AWS_S3 -> new AWSS3Source();
            case AMQP -> new AMQPSource(uri, queue);
            default -> throw new IllegalStateException("Unexpected value: " + supportedType);
        };
    }

    private static Target target(SupportedType supportedType, String uri, String queue) {
        return switch(supportedType) {
            case AMQP -> new AMQPTarget(uri, queue);
            default -> throw new IllegalStateException("Unexpected value: " + supportedType);
        };
    }

}
