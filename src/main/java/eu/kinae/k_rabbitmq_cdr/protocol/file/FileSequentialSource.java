package eu.kinae.k_rabbitmq_cdr.protocol.file;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class FileSequentialSource extends AbstractComponentSource {

    public FileSequentialSource(FileReader source, SharedQueue target, KOptions options) {
        super(source, target, options);
    }

}
