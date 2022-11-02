package eu.kinae.k_rabbitmq_cdr.component.aws;

import eu.kinae.k_rabbitmq_cdr.component.Target;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;
import eu.kinae.k_rabbitmq_cdr.utils.CustomObjectMapper;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

public class AWS_S3Writer implements Target {

    private static final Logger logger = LoggerFactory.getLogger(AWS_S3Writer.class);

    private final S3Client s3;
    private final String bucket;
    private final String prefix;
    private final SharedStatus sharedStatus;

    public AWS_S3Writer(S3Client s3, String bucket, String prefix) {
        this(s3, bucket, prefix, null);
    }

    public AWS_S3Writer(S3Client s3, String bucket, String prefix, SharedStatus progressStatus) {
        this.s3 = s3;
        this.bucket = bucket;
        this.prefix = AWS_S3ClientBuilder.buildPrefix(prefix);
        this.sharedStatus = progressStatus;
        logger.info("writing files in {} with prefix {}", bucket, prefix);
    }

    @Override
    public void push(KMessage message) throws Exception {
        String filename = Constant.FILE_PREFIX + message.deliveryTag();
        s3.putObject(it -> it.bucket(bucket).key(prefix + filename), RequestBody.fromBytes(message.body()));
        if(message.properties() != null) {
            RequestBody props = RequestBody.fromBytes(CustomObjectMapper.om.writeValueAsBytes(message.properties()));
            s3.putObject(it -> it.bucket(bucket).key(prefix + filename + Constant.FILE_PROPERTIES_SUFFIX), props);
        }
        if(sharedStatus != null) {
            sharedStatus.incrementWrite();
        }
    }

    @Override
    public void close() {
    }

}
