package eu.kinae.k_rabbitmq_cdr.utils;

public class SourceParams {

    private final long maxMessage; // total number of message to get;
    //private Set<Integer> specificMessageToLoad;

    public SourceParams() {
        this(0);
    }

    public SourceParams(long maxMessage) {
        if(maxMessage < 0) {
            throw new IllegalArgumentException("Parameter MaxMessage must be >= 0");
        }
        this.maxMessage = maxMessage;
    }

    public long getMaxMessage() {
        return maxMessage;
    }
}
