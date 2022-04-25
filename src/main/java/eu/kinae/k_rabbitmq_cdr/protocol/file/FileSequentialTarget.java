package eu.kinae.k_rabbitmq_cdr.protocol.file;

import eu.kinae.k_rabbitmq_cdr.protocol.Source;

public class FileSequentialTarget extends FileComponentTarget {

    public FileSequentialTarget(Source source, FileWriterTarget target) {
        super(source, target);
    }

    @Override
    protected boolean stopConsumingIfResponseIsNull() {
        return true;
    }

}
