package eu.kinae.k_rabbitmq_cdr.params;

public record KParameters(SupportedType sourceType, String sourceURI, String sourceQueue,
                          SupportedType targetType, String targetURI, String targetQueue,
                          String directory, String bucket, String prefix,
                          TransferType transferType, ProcessType processType) {

}
