package eu.kinae.k_rabbitmq_cdr.utils;

import java.util.ArrayList;
import java.util.List;

public class SharedStatus {

    private static SharedStatus instance = null;

    private MessageStatus source;
    private final List<MessageStatus> target = new ArrayList<>();
    private volatile boolean consumerAlive = true;

    private SharedStatus() {
    }

    public static SharedStatus getInstance() {
        if(instance == null) {
            instance = new SharedStatus();
        }
        return instance;
    }

    public MessageStatus getSource() {
        return source;
    }

    public void addSource(String name, long amount) {
        this.source = new MessageStatus(name, amount);
    }

    public List<MessageStatus> getTarget() {
        return target;
    }

    public void addTarget(MessageStatus target) {
        this.target.add(target);
    }

    // name if bad, can not find better for now
    public void notifySourceConsumerIsDone() {
        this.consumerAlive = false;
    }

    public boolean isConsumerAlive() {
        return consumerAlive;
    }

}
