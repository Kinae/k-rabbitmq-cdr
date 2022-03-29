package eu.kinae.k_rabbitmq_cdr.params;

import com.beust.jcommander.Parameter;

public class JCommanderParams {

    @Parameter(names = { "-h", "--help" }, description = "Display help", help = true, order = 0)
    public boolean help;

    @Parameter(names = { "-v", "-verbose" }, description = "Level of verbosity", order = 1)
    public Integer verbose = 1;

    @Parameter(names = { "-st", "--source-type" }, description = "Type of the source", required = true, order = 2)
    public SupportedType sourceType;

    @Parameter(names = { "-s-uri", "--source-uri" }, description = "URI of the source", required = true, password = true, order = 3)
    public String sourceURI;

    @Parameter(names = { "-sq", "--source-queue" }, description = "Queue of the source", order = 4)
    public String sourceQueue;

    @Parameter(names = { "-tt", "--target-type" }, description = "Type of the target", required = true, order = 5)
    public SupportedType targetType;

    @Parameter(names = { "-t-uri", "--to-uri" }, description = "URI of the target", required = true, password = true, order = 6)
    public String targetURI;

    @Parameter(names = { "-tq", "--target-queue" }, description = "Queue of the target", order = 7)
    public String targetQueue;

}
