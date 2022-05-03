package eu.kinae.k_rabbitmq_cdr.params;

import com.beust.jcommander.Parameter;

public class JCommanderParams {

    @Parameter(names = { "-h", "--help" }, description = "Display help", help = true, order = 0)
    public boolean help;

    @Parameter(names = { "-v", "-verbose" }, description = "Level of verbosity", order = 1)
    public Integer verbose = 1;

    @Parameter(names = { "--source-type" }, description = "Type of the source", required = true, order = 2)
    public SupportedType sourceType;

    @Parameter(names = { "--source-uri" }, description = "URI of the source", required = true, password = true, order = 3)
    public String sourceURI;

    @Parameter(names = { "--source-queue" }, description = "Queue of the source", order = 4)
    public String sourceQueue;

    @Parameter(names = { "--target-type" }, description = "Type of the target", required = true, order = 5)
    public SupportedType targetType;

    @Parameter(names = { "--to-uri" }, description = "URI of the target", required = true, password = true, order = 6)
    public String targetURI;

    @Parameter(names = { "--target-queue" }, description = "Queue of the target", order = 7)
    public String targetQueue;

    @Parameter(names = { "-dir", "--directory" }, description = "Files directory", order = 8)
    public String directory;

    @Parameter(names = { "--bucket" }, description = "Bucket name for AWS_S3 connector", order = 9)
    public String bucket;

    @Parameter(names = { "--prefix" }, description = "Key's prefix for AWS_S connector", order = 10)
    public String prefix;

    @Parameter(names = { "--transfer-type" }, description = "Type of transfer", order = 11)
    public TransferType transferType = TransferType.BUFFER;

    @Parameter(names = { "--process-type" }, description = "Type of process to use when using BUFFER as transferType", order = 12)
    public ProcessType processType = ProcessType.PARALLEL;

    @Parameter(names = { "-max", "--max-messages" }, description = "Maximum number of messages (0 for all)", order = 13)
    public int maxMessage = 0;

    @Parameter(names = { "-th", "--thread" }, description = "Number of threads when process-type is PARALLEL", order = 14)
    public int threads = 1;

    @Parameter(names = { "--sorted" }, description = "Sort messages listed before processing. Used for source-type FILE/AWS_S3." +
            " Has no effect if process-type is PARALLEL with more than 1 thread", order = 15)
    public boolean sorted;

}
