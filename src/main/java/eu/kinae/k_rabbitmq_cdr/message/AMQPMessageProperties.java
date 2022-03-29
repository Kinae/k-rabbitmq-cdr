package eu.kinae.k_rabbitmq_cdr.message;

import java.util.Date;
import java.util.Map;

public class AMQPMessageProperties {

    private String contentType;
    private String contentEncoding;
    private Map<String, Object> headers;
    private Integer deliveryMode;
    private Integer priority;
    private String correlationId;
    private String replyTo;
    private String expiration;
    private String messageId;
    private Date timestamp;
    private String type;
    private String userId;
    private String appId;
    private String clusterId;

}
