package eu.kinae.k_rabbitmq_cdr.params;

public record KParameters(SupportedType sourceType, String sourceURI, String sourceQueue,
                          SupportedType targetType, String targetURI, String targetQueue,
                          TransferType transferType, ProcessType processType) {

    public KParameters(SupportedType sourceType, String sourceURI, String sourceQueue, SupportedType targetType, String targetURI, String targetQueue) {
        this(sourceType, sourceURI, sourceQueue, targetType, targetURI, targetQueue, TransferType.BUFFER, ProcessType.SEQUENTIAL);
    }
}
