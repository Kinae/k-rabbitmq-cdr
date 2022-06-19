package eu.kinae.k_rabbitmq_cdr.params;

public record KOptions(int maxMessage, int threads, boolean sorted, int interval, boolean bodyOnly) {

    public static final KOptions DEFAULT = new KOptions(0, 4, false, 2000, false);
    public static final KOptions SORTED = new KOptions(0, 4, true, 2000, false);
    public static final KOptions BODY_ONLY = new KOptions(0, 4, false, 2000, true);
    public static final KOptions SORTED_BODY_ONLY = new KOptions(0, 4, true, 2000, true);

    public KOptions(int maxMessage) {
        this(maxMessage, 4, false, 2000, false);
    }

    public static KOptions of(JCommanderParams jParams) {
        return new KOptions(jParams.maxMessage, jParams.threads, jParams.sorted, jParams.interval, jParams.bodyOnly);
    }
}
