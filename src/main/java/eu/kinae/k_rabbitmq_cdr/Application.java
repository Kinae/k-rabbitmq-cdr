package eu.kinae.k_rabbitmq_cdr;


import com.beust.jcommander.JCommander;
import eu.kinae.k_rabbitmq_cdr.from.FromAMQP;
import eu.kinae.k_rabbitmq_cdr.from.FromAWSS3;
import eu.kinae.k_rabbitmq_cdr.from.FromFile;
import eu.kinae.k_rabbitmq_cdr.from.Source;
import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;

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

        System.out.println("Verbose: " + params.verbose);
        System.out.println("toUri: " + params.toURI);
        System.out.println("fromUri: " + params.fromURI);

        Source source = from(params.fromType, params.fromURI, "notification-dlx");
        source.run();
    }

    private static Source from(SupportedType supportedType, String uri, String queue) {
        return switch(supportedType) {
            case FILE -> new FromFile();
            case AWS_S3 -> new FromAWSS3();
            case AMQP -> new FromAMQP(uri, queue);
            default -> throw new IllegalStateException("Unexpected value: " + supportedType);
        };
    }

}
