package eu.kinae.k_rabbitmq_cdr.params;

public record KParameters(SupportedType sourceType, String sourceURI, String sourceQueue, String input,
                          SupportedType targetType, String targetURI, String targetQueue, String output,
                          TransferType transferType, ProcessType processType) {

    public KParameters(SupportedType sourceType, String sourceURI, String sourceQueue, String input, SupportedType targetType, String targetURI, String targetQueue, String output) {
        this(sourceType, sourceURI, sourceQueue, input, targetType, targetURI, targetQueue, output, TransferType.BUFFER, ProcessType.SEQUENTIAL);
    }

}
