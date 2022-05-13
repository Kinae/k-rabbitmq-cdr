package eu.kinae.k_rabbitmq_cdr.params;

public record KOptions(int maxMessage, int threads, boolean sorted, int interval) {

    public static final KOptions DEFAULT = new KOptions(0, 1, false, 2000);

    public KOptions(int maxMessage) {
        this(maxMessage, 1, false, 2000);
    }

    public static KOptions of(JCommanderParams jParams) {
        return new KOptions(jParams.maxMessage, jParams.threads, jParams.sorted, jParams.interval);
    }
}
