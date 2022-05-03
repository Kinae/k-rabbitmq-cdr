package eu.kinae.k_rabbitmq_cdr.params;

import software.amazon.awssdk.regions.Region;

public record KParameters(SupportedType sourceType, String sourceURI, String sourceQueue,
                          SupportedType targetType, String targetURI, String targetQueue,
                          String directory, Region region, String bucket, String prefix,
                          TransferType transferType, ProcessType processType) {

}
