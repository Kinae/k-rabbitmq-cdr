package eu.kinae.k_rabbitmq_cdr.protocol.file;

import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;

abstract class FileComponentTarget extends FileComponent {

    public FileComponentTarget(Source source, FileWriterTarget target) {
        super(source, target);
    }

    @Override
    public void push(KMessage message) throws Exception {
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    protected void onFinally() {

    }

    @Override
    public long consumeNProduce() throws Exception {
        long count = 0;
        do {
            KMessage message = pop();
            if(message == null) {
                logger.debug("Waiting for message ...");
                if(stopConsumingIfResponseIsNull())
                    break;
            } else {
                count++;
                push(message);
            }
        } while(true);
        return count;
    }

    protected abstract boolean stopConsumingIfResponseIsNull();

}
