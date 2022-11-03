package eu.kinae.k_rabbitmq_cdr;

import java.util.Optional;

import com.beust.jcommander.JCommander;
import eu.kinae.k_rabbitmq_cdr.connector.Connector;
import eu.kinae.k_rabbitmq_cdr.connector.ConnectorFactory;
import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.params.JCommanderParamsValidator;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KRabbitMQCDR {

    private static final Logger logger = LoggerFactory.getLogger(KRabbitMQCDR.class);

    private final JCommanderParams jParams = new JCommanderParams();

    public KRabbitMQCDR() {
    }

    public Optional<KRabbitMQCDR> init(String[] args) {
        JCommander jct = JCommander.newBuilder().addObject(jParams).build();
        jct.parse(args);

        if(jParams.help) {
            jct.usage();
            return Optional.empty();
        }

        JCommanderParamsValidator.validate(jParams);
        return Optional.of(this);
    }

    public void start() {
        KParameters parameters = KParameters.of(jParams);
        KOptions options = KOptions.of(jParams);

        try(Connector connector = ConnectorFactory.newConnector(parameters, options)) {
            if(connector != null) connector.init(parameters, options).start(parameters, options);
            else logger.error("No connector found for {} => {}", parameters.sourceType(), parameters.targetType());
        } catch(Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        }
    }

}
