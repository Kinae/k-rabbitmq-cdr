package eu.kinae.k_rabbitmq_cdr.protocol.file;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class FileSequentialSource extends FileComponentSource implements Source {

    public FileSequentialSource(FileReaderSource source, SharedQueue target) {
        super(source, target, KOptions.DEFAULT);
    }

    public FileSequentialSource(FileReaderSource source, SharedQueue target, KOptions options) {
        super(source, target, options);
    }

}
