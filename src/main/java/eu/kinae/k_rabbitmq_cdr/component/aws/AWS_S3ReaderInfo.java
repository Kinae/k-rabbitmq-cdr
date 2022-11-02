package eu.kinae.k_rabbitmq_cdr.component.aws;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

public class AWS_S3ReaderInfo implements AutoCloseable {

    private final S3Client s3;
    private final String bucket;
    private final String prefix;
    private final BlockingQueue<S3Object> queue;
    private final Logger logger = LoggerFactory.getLogger(AWS_S3ReaderInfo.class);

    public AWS_S3ReaderInfo(KParameters parameters, KOptions options) {
        this(AWS_S3ClientBuilder.build(parameters), parameters.bucket(), parameters.prefix(), options);
    }

    public AWS_S3ReaderInfo(S3Client s3, String bucket, String prefix, KOptions options) {
        this.s3 = s3;
        this.bucket = bucket;
        this.prefix = prefix;

        logger.info("listing files in {} with prefix {}", bucket, prefix);
        ListObjectsV2Request.Builder request = ListObjectsV2Request.builder()
            .bucket(bucket)
            .prefix(AWS_S3ClientBuilder.buildPrefix(prefix))
            .delimiter("/");
        ListObjectsV2Response response;
        List<S3Object> s3Objects = new ArrayList<>();
        do {
            response = s3.listObjectsV2(request.build());
            s3Objects.addAll(response.contents().stream().filter(it -> !it.key().endsWith(Constant.FILE_PROPERTIES_SUFFIX)).collect(Collectors.toList()));
            request.continuationToken(response.nextContinuationToken());
        } while(response.isTruncated());

        logger.info("number of files listed : {}", s3Objects.size());
        Stream<S3Object> s3ObjectStream = s3Objects.stream();
        if(options.sorted()) {
            logger.info("sorting AWS_S3 filename by ascending number");
            s3ObjectStream = s3ObjectStream.sorted(Comparator.comparing(it -> Constant.extractDeliveryTagFromKey(prefix, it.key())));
        }

        queue = s3ObjectStream.collect(Collectors.toCollection(LinkedBlockingQueue::new));
    }



    public long countMessages() {
        return queue.size();
    }


    public S3Object pop() throws Exception {
        return queue.poll(500, TimeUnit.MILLISECONDS);
    }

    public S3Client getS3() {
        return s3;
    }

    public String getBucket() {
        return bucket;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public void close() {
        s3.close();
    }

}
