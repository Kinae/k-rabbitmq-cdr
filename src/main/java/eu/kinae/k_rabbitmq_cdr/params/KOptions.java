package eu.kinae.k_rabbitmq_cdr.params;

import java.util.Collections;
import java.util.Set;

public record KOptions(int maxMessage, Set<Integer> specificMessagesToGet, int threads, boolean sorted) {

    public static final KOptions DEFAULT = new KOptions(0, Collections.emptySet(), 1, false);

    public KOptions(int maxMessage) {
        this(maxMessage, Collections.emptySet(), 1, false);
    }
}
