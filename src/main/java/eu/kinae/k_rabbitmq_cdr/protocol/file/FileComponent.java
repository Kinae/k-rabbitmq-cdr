package eu.kinae.k_rabbitmq_cdr.protocol.file;

import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.protocol.Component;

public abstract class FileComponent implements Component {

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.FILE;
    }

}
