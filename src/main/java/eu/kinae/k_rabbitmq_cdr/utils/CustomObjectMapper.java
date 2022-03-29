package eu.kinae.k_rabbitmq_cdr.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.rabbitmq.client.LongString;

public final class CustomObjectMapper {

    public static final ObjectMapper om = tt();

    private static ObjectMapper tt() {
        ObjectMapper om = new ObjectMapper();
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        SimpleModule amqpModule = new SimpleModule("AMQPModule", Version.unknownVersion());
        amqpModule.addSerializer(LongString.class, new LongStringSerializer());
        om.registerModule(amqpModule);

        return om;
    }

    private static class LongStringSerializer extends JsonSerializer<LongString> {

        @Override
        public void serialize(LongString value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(new String(value.getBytes()));
        }
    }

}
