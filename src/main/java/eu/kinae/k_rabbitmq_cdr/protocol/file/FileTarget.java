package eu.kinae.k_rabbitmq_cdr.protocol.file;

import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;

public class FileTarget extends FileComponent implements Target {

    public FileTarget() {
    }

    @Override public void push(KMessage message) throws Exception {

    }

}
