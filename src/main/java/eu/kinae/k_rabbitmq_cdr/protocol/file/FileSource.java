package eu.kinae.k_rabbitmq_cdr.protocol.file;

import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;

public class FileSource extends FileComponent implements Source {

    public FileSource() {
    }

    @Override public KMessage pop() throws Exception {
        return null;
    }

    @Override public void close() throws Exception {

    }

    //    @Override
    //    public boolean consume() throws Exception {
    //        return false;
    //    }
}
