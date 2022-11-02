package eu.kinae.k_rabbitmq_cdr.component.aws;

import java.io.ByteArrayInputStream;

import com.rabbitmq.client.AMQP;
import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;
import eu.kinae.k_rabbitmq_cdr.utils.CustomObjectMapper;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import software.amazon.awssdk.services.s3.model.S3Object;

public class AWS_S3Reader implements Source {

    private final AWS_S3ReaderInfo awsS3Client;
    private final SharedStatus sharedStatus;

    public AWS_S3Reader(AWS_S3ReaderInfo awsS3Client) {
        this(awsS3Client, null);
    }

    public AWS_S3Reader(AWS_S3ReaderInfo awsS3Client, SharedStatus sharedStatus) {
        this.awsS3Client = awsS3Client;
        this.sharedStatus = sharedStatus;
    }

    @Override
    public KMessage pop(KOptions options) throws Exception {
        S3Object s3Object = awsS3Client.pop();
        if(s3Object == null) {
            return null;
        }

        byte[] body = awsS3Client.getS3().getObjectAsBytes(it -> it.bucket(awsS3Client.getBucket()).key(s3Object.key())).asByteArray();
        if(sharedStatus != null) {
            sharedStatus.incrementRead();
        }
        AMQP.BasicProperties props = null;
        if(!options.bodyOnly()) {
            byte[] properties = awsS3Client.getS3().getObjectAsBytes(it -> it.bucket(awsS3Client.getBucket()).key(s3Object.key() + Constant.FILE_PROPERTIES_SUFFIX).build()).asByteArray();
            props = CustomObjectMapper.om.readValue(new ByteArrayInputStream(properties), AMQP.BasicProperties.class);
        }
        long deliveryTag = Constant.extractDeliveryTagFromKey(awsS3Client.getPrefix(), s3Object.key());
        return new KMessage(props, body, deliveryTag);
    }

    @Override
    public void close() {
    }

}
