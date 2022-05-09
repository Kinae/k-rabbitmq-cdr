package eu.kinae.k_rabbitmq_cdr.component.file;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class FileSequentialSource extends AbstractComponentSource {

    public FileSequentialSource(FileReader source, SharedQueue target, KOptions options) {
        super(source, target, options);
    }

}
