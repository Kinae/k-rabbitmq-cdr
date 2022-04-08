package eu.kinae.k_rabbitmq_cdr.utils;

public class KOptions {

    private final long maxMessage; // total number of message to get;
    //private Set<Integer> specificMessageToLoad;

    public KOptions() {
        this(0);
    }

    public KOptions(long maxMessage) {
        if(maxMessage < 0) {
            throw new IllegalArgumentException("Parameter MaxMessage must be >= 0");
        }
        this.maxMessage = maxMessage;
    }

    public long getMaxMessage() {
        return maxMessage;
    }
}
