package eu.kinae.k_rabbitmq_cdr.protocol.aws;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.rabbitmq.client.AMQP;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;
import eu.kinae.k_rabbitmq_cdr.utils.CustomObjectMapper;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

public class AWS_S3Reader implements Source {

    private static final Logger logger = LoggerFactory.getLogger(AWS_S3Reader.class);

    private final S3Client s3;
    private final String bucket;
    private final String prefix;
    private final Iterator<S3Object> it;

    public AWS_S3Reader(S3Client s3, String bucket, String prefix, KOptions options) {
        this.s3 = s3;
        this.bucket = bucket;
        this.prefix = prefix;

        logger.info("listing files ...");
        ListObjectsV2Request.Builder request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(buildPrefix(prefix))
                .delimiter("/");
        ListObjectsV2Response response;
        List<S3Object> s3Objects = new ArrayList<>();
        do {
            response = s3.listObjectsV2(request.build());
            s3Objects.addAll(response.contents().stream().filter(it -> !it.key().endsWith(Constant.FILE_PROPERTIES_SUFFIX)).collect(Collectors.toList()));
            request.continuationToken(response.nextContinuationToken());
        } while(response.isTruncated());

        logger.info("number of files listed : {}", s3Objects.size());
        if(options.sorted())
            it = s3Objects.stream().sorted(Comparator.comparing(it -> extractDeliveryTagFromKey(it.key()))).iterator();
        else
            it = s3Objects.iterator();
    }

    @Override
    public KMessage pop() throws Exception {
        if(!it.hasNext())
            return null;

        S3Object s3Object = it.next();
        byte[] body = s3.getObjectAsBytes(it -> it.bucket(bucket).key(s3Object.key())).asByteArray();
        byte[] properties = s3.getObjectAsBytes(it -> it.bucket(bucket).key(s3Object.key() + Constant.FILE_PROPERTIES_SUFFIX).build()).asByteArray();
        AMQP.BasicProperties props = CustomObjectMapper.om.readValue(new ByteArrayInputStream(properties), AMQP.BasicProperties.class);
        long deliveryTag = extractDeliveryTagFromKey(s3Object.key());
        return new KMessage(props, body, deliveryTag);
    }

    @Override
    public void close() {
    }

    private String buildPrefix(String key2) {
        if(key2.endsWith("/"))
            return key2;
        return key2 + "/";
    }

    private long extractDeliveryTagFromKey(String key) {
        return Long.parseLong(key.substring(prefix.length() + Constant.FILE_PREFIX.length() + 1));
    }
}
