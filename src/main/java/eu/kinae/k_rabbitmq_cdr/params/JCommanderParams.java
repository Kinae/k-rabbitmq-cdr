package eu.kinae.k_rabbitmq_cdr.params;

import com.beust.jcommander.Parameter;
import software.amazon.awssdk.regions.Region;

public class JCommanderParams {

    @Parameter(names = { "--help" }, description = "Display help", help = true)
    public boolean help;

    @Parameter(names = { "--interval" }, description = "Specify the progression update interval in milliseconds (default 2000)")
    public int interval = 2000;

    @Parameter(names = { "--source-type" }, description = "Type of the source", required = true)
    public SupportedType sourceType;

    @Parameter(names = { "--source-uri" }, description = "URI of the source for AMQP connector", password = true)
    public String sourceURI;

    @Parameter(names = { "--source-queue" }, description = "Queue of the source for AMQP connector")
    public String sourceQueue;

    @Parameter(names = { "--target-type" }, description = "Type of the target", required = true)
    public SupportedType targetType;

    @Parameter(names = { "--to-uri" }, description = "URI of the target for AMQP connector", password = true)
    public String targetURI;

    @Parameter(names = { "--target-queue" }, description = "Queue of the target for AMQP connector")
    public String targetQueue;

    @Parameter(names = { "--directory" }, description = "Path of the directory to use to load/save messages for FILE connector")
    public String directory;

    @Parameter(names = { "--region" }, description = "Region for AWS_S3 connector", converter = RegionConverter.class)
    public Region region;

    @Parameter(names = { "--bucket" }, description = "Bucket name for AWS_S3 connector")
    public String bucket;

    @Parameter(names = { "--prefix" }, description = "Key's prefix for AWS_S3 connector")
    public String prefix;

    @Parameter(names = { "--profile" }, description = "Profile for AWS_S3 connector")
    public String profile;

    @Parameter(names = { "--transfer-type" }, description = "Type of transfer")
    public TransferType transferType = TransferType.BUFFERED;

    @Parameter(names = { "--process-type" }, description = "Type of process to use when using BUFFERED as transfer-type")
    public ProcessType processType = ProcessType.PARALLEL;

    @Parameter(names = { "--max-messages" }, description = "Maximum number of messages (0 for all)")
    public int maxMessage = 0;

    @Parameter(names = { "--thread" }, description = "Number of threads when using PARALLEL as process-type")
    public int threads = 4;

    @Parameter(names = { "--sorted" }, description = "Sort messages listed before processing. Used for source-type FILE/AWS_S3." +
            " Has no effect if process-type is PARALLEL with more than 1 thread")
    public boolean sorted;

}
