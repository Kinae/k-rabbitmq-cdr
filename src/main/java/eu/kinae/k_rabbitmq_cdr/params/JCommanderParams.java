package eu.kinae.k_rabbitmq_cdr.params;

import com.beust.jcommander.Parameter;
import software.amazon.awssdk.regions.Region;

public class JCommanderParams {

    @Parameter(names = { "-h", "--help" }, description = "Display help", help = true, order = 0)
    public boolean help;

    @Parameter(names = { "-v", "-verbose" }, description = "Level of verbosity", order = 1)
    public Integer verbose = 1;

    @Parameter(names = { "--source-type" }, description = "Type of the source", required = true, order = 2)
    public SupportedType sourceType;

    @Parameter(names = { "--source-uri" }, description = "URI of the source for AMQP connector", required = true, password = true, order = 3)
    public String sourceURI;

    @Parameter(names = { "--source-queue" }, description = "Queue of the source for AMQP connector", order = 4)
    public String sourceQueue;

    @Parameter(names = { "--target-type" }, description = "Type of the target", required = true, order = 5)
    public SupportedType targetType;

    @Parameter(names = { "--to-uri" }, description = "URI of the target for AMQP connector", required = true, password = true, order = 6)
    public String targetURI;

    @Parameter(names = { "--target-queue" }, description = "Queue of the target for AMQP connector", order = 7)
    public String targetQueue;

    @Parameter(names = { "-dir", "--directory" }, description = "Files directory for FILE connector", order = 8)
    public String directory;

    @Parameter(names = { "--region" }, description = "Region for AWS_S3 connector", order = 9, converter = RegionConverter.class)
    public Region region;

    @Parameter(names = { "--bucket" }, description = "Bucket name for AWS_S3 connector", order = 10)
    public String bucket;

    @Parameter(names = { "--prefix" }, description = "Key's prefix for AWS_S3 connector", order = 11)
    public String prefix;

    @Parameter(names = { "--transfer-type" }, description = "Type of transfer", order = 12)
    public TransferType transferType = TransferType.DIRECT;

    @Parameter(names = { "--process-type" }, description = "Type of process to use when using BUFFERED as transfer-type", order = 13)
    public ProcessType processType = ProcessType.SEQUENTIAL;

    @Parameter(names = { "-max", "--max-messages" }, description = "Maximum number of messages (0 for all)", order = 14)
    public int maxMessage = 0;

    @Parameter(names = { "-th", "--thread" }, description = "Number of threads when using PARALLEL as process-type", order = 15)
    public int threads = 1;

    @Parameter(names = { "--sorted" }, description = "Sort messages listed before processing. Used for source-type FILE/AWS_S3." +
            " Has no effect if process-type is PARALLEL with more than 1 thread", order = 16)
    public boolean sorted;

}
