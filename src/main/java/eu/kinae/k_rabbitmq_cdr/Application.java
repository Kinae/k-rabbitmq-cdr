package eu.kinae.k_rabbitmq_cdr;


public final class Application {

    private Application() {
    }

    public static void main(String[] args) {
        new KRabbitMQCDR().init(args).ifPresent(KRabbitMQCDR::start);
    }
}
