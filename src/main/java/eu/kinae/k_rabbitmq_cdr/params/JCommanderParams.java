package eu.kinae.k_rabbitmq_cdr.params;

import com.beust.jcommander.Parameter;

public class JCommanderParams {

    @Parameter(names = { "-h", "--help" }, description = "Display help", help = true, order = 0)
    public boolean help;

    @Parameter(names = { "-v", "-verbose" }, description = "Level of verbosity", order = 1)
    public Integer verbose = 1;

    @Parameter(names = { "--from-type" }, description = "Type of the source", required = true, order = 2)
    public SupportedType fromType;

    @Parameter(names = { "--from-uri" }, description = "URI of the source", required = true, password = true, order = 3)
    public String fromURI;

    @Parameter(names = { "--to-type" }, description = "Type of the target", required = true, order = 4)
    public SupportedType toType;

    @Parameter(names = { "--to-uri" }, description = "URI of the target", required = true, password = true, order = 5)
    public Integer toURI;

}
