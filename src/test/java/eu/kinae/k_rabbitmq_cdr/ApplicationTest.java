package eu.kinae.k_rabbitmq_cdr;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationTest {

    @Test
    public void Application_print_help() {
        Optional<KRabbitMQCDR> optionalApp = new KRabbitMQCDR().init(new String[]{"--help"});
        assertThat(optionalApp).isEmpty();
    }

    @Test
    public void Application_does_init() {
        Optional<KRabbitMQCDR> optionalApp = new KRabbitMQCDR().init(new String[]{
            "--source-type", "AMQP", "--source-uri",  "amqp://admin:admin@localhost:5672/%2F", "--source-queue", "sourceQ",
            "--target-type", "FILE", "--directory",  "/tmp"});
        assertThat(optionalApp).isPresent();
    }
}
