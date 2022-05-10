package eu.kinae.k_rabbitmq_cdr.params;

import software.amazon.awssdk.regions.Region;

public record KParameters(SupportedType sourceType, String sourceURI, String sourceQueue,
                          SupportedType targetType, String targetURI, String targetQueue,
                          String directory, Region region, String bucket, String prefix, String profile,
                          TransferType transferType, ProcessType processType) {

    public static KParameters of(JCommanderParams jParams) {
        return new KParameters(jParams.sourceType, jParams.sourceURI, jParams.sourceQueue,
                               jParams.targetType, jParams.targetURI, jParams.targetQueue,
                               jParams.directory, jParams.region, jParams.bucket, jParams.prefix, jParams.profile,
                               jParams.transferType, jParams.processType);
    }

}
