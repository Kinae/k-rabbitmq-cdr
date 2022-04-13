package eu.kinae.k_rabbitmq_cdr.params;

import java.util.Collections;
import java.util.Set;

public record KOptions(long maxMessage, Set<Integer> specificMessagesToGet) {

    public KOptions() {
        this(0, Collections.emptySet());
    }

    public KOptions(long maxMessage) {
        this(maxMessage, Collections.emptySet());
    }

    public KOptions(Set<Integer> specificMessagesToGet) {
        this(0, specificMessagesToGet);
    }
}
