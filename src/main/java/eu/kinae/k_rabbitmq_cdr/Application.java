package eu.kinae.k_rabbitmq_cdr;

import com.beust.jcommander.JCommander;
import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.source.AMQPSource;
import eu.kinae.k_rabbitmq_cdr.source.AWSS3Source;
import eu.kinae.k_rabbitmq_cdr.source.FileSource;
import eu.kinae.k_rabbitmq_cdr.source.Source;

public final class Application {

    private Application() {
    }

    // gradle run --args="-from rabbitmq:notification-dlx-exchange?queue=notification-dlx&concurrentConsumers=1 -to file:./out.tmp"
    public static void main(String[] args) throws Exception {
        JCommanderParams params = new JCommanderParams();
        JCommander jct = JCommander.newBuilder().addObject(params).build();
        jct.parse(args);

        if(params.help) {
            jct.usage();
            return;
        }

        Source source = from(params.sourceType, params.sourceURI, params.sourceQueue);
        source.run();
    }

    private static Source from(SupportedType supportedType, String uri, String queue) {
        return switch(supportedType) {
            case FILE -> new FileSource();
            case AWS_S3 -> new AWSS3Source();
            case AMQP -> new AMQPSource(uri, queue);
            default -> throw new IllegalStateException("Unexpected value: " + supportedType);
        };
    }

}
