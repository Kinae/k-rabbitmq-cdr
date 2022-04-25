package eu.kinae.k_rabbitmq_cdr.protocol.file;

import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.protocol.Component;

public interface FileComponent extends Component {

    @Override
    default SupportedType getSupportedType() {
        return SupportedType.FILE;
    }
}
