package eu.kinae.k_rabbitmq_cdr;

import java.nio.file.Files;
import java.nio.file.Path;

import com.beust.jcommander.JCommander;
import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.source.AMQPSource;
import eu.kinae.k_rabbitmq_cdr.source.AWSS3Source;
import eu.kinae.k_rabbitmq_cdr.source.FileSource;
import eu.kinae.k_rabbitmq_cdr.source.Source;
import eu.kinae.k_rabbitmq_cdr.target.AMQPTarget;
import eu.kinae.k_rabbitmq_cdr.target.Target;

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

        var timestamp = System.currentTimeMillis();
        String tmp = System.getProperty("java.io.tmpdir");
        Path dir = Files.createDirectory(Path.of(tmp + "/k-rabbitmq-cdr_" + timestamp));
//        dir.toFile().deleteOnExit();
        Source source = source(params.sourceType, params.sourceURI, params.sourceQueue, dir);
        source.run();

        Target target = target(params.targetType, params.targetURI, params.targetQueue, dir);
        target.run();
    }

    private static Source source(SupportedType supportedType, String uri, String queue, Path dir) {
        return switch(supportedType) {
            case FILE -> new FileSource();
            case AWS_S3 -> new AWSS3Source();
            case AMQP -> new AMQPSource(dir, uri, queue);
            default -> throw new IllegalStateException("Unexpected value: " + supportedType);
        };
    }

    private static Target target(SupportedType supportedType, String uri, String queue, Path dir) {
        return switch(supportedType) {
            case AMQP -> new AMQPTarget(dir, uri, queue);
            default -> throw new IllegalStateException("Unexpected value: " + supportedType);
        };
    }

}
