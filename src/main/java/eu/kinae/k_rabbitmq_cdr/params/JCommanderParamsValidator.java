package eu.kinae.k_rabbitmq_cdr.params;

import eu.kinae.k_rabbitmq_cdr.connector.ConnectorFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.Pair;

public class JCommanderParamsValidator {

    private static final String SOURCE = "source";
    private static final String TARGET = "target";
    private static final String ERROR_MESSAGE = "%s must be specified when using --%s-type %s";

    public static void validate(JCommanderParams jParams) {
        Pair<SupportedType, SupportedType> pair = Pair.of(jParams.sourceType, jParams.targetType);
        if(!ConnectorFactory.connectorsAvailable.contains(pair)) {
            throw new IllegalArgumentException(String.format("Connector between %s is not supported", pair));
        }

        amqp(jParams.sourceType, jParams.sourceURI, jParams.sourceQueue, SOURCE);
        amqp(jParams.targetType, jParams.targetURI, jParams.targetQueue, TARGET);
        file(jParams.sourceType, jParams.directory, SOURCE);
        file(jParams.targetType, jParams.directory, TARGET);
        awsS3(jParams.sourceType, jParams.region, jParams.bucket, jParams.prefix, SOURCE);
        awsS3(jParams.targetType, jParams.region, jParams.bucket, jParams.prefix, TARGET);

        if(jParams.processType == ProcessType.PARALLEL && jParams.threads > 1) {
            jParams.sorted = false;
        }
    }

    private static void amqp(SupportedType supportedType, String uri, String queue, String type) {
        if(supportedType != SupportedType.AMQP)
            return;
        if(uri == null)
            throw new IllegalArgumentException(String.format(ERROR_MESSAGE, String.format("--%s-uri", type), type, SupportedType.AMQP));
        if(queue == null)
            throw new IllegalArgumentException(String.format(ERROR_MESSAGE, String.format("--%s-queue", type), type, SupportedType.AMQP));
    }

    private static void file(SupportedType supportedType, String dir, String type) {
        if(supportedType != SupportedType.FILE)
            return;
        if(dir == null)
            throw new IllegalArgumentException(String.format(ERROR_MESSAGE, "-dir/--directory", type, SupportedType.FILE));
    }

    private static void awsS3(SupportedType supportedType, Region region, String bucket, String prefix, String type) {
        if(supportedType != SupportedType.AWS_S3)
            return;
        if(region == null)
            throw new IllegalArgumentException(String.format(ERROR_MESSAGE, "--region", type, SupportedType.AWS_S3));
        if(bucket == null)
            throw new IllegalArgumentException(String.format(ERROR_MESSAGE, "--bucket", type, SupportedType.AWS_S3));
        if(prefix == null)
            throw new IllegalArgumentException(String.format(ERROR_MESSAGE, "--prefix", type, SupportedType.AWS_S3));
    }

}
