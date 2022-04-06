package eu.kinae.k_rabbitmq_cdr.protocol.file;

import eu.kinae.k_rabbitmq_cdr.protocol.Target;

public class FileTarget extends FileComponent implements Target {

    public FileTarget() {
    }

    @Override
    public boolean publish() throws Exception {
        return false;
    }
}
