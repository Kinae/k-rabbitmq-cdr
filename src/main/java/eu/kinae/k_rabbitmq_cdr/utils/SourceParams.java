package eu.kinae.k_rabbitmq_cdr.utils;

public class SourceParams {

    private final long totalMessage; // total number of message to get;
    //private Set<Integer> specificMessageToLoad;

    public SourceParams() {
        this(-1);
    }

    public SourceParams(long totalMessage) {
        this.totalMessage = totalMessage;
    }

    public long getTotalMessage() {
        return totalMessage;
    }
}
