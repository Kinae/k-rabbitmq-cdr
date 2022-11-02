package eu.kinae.k_rabbitmq_cdr.component.file;

import java.io.File;
import java.nio.file.Files;

import com.rabbitmq.client.AMQP;
import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;
import eu.kinae.k_rabbitmq_cdr.utils.CustomObjectMapper;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class FileReader implements Source {

    private final SharedStatus sharedStatus;
    private final FileReaderInfo filePathInfo;

    public FileReader(FileReaderInfo filePathInfo) {
        this(filePathInfo, null);
    }

    public FileReader(FileReaderInfo filePathInfo, SharedStatus sharedStatus) {
        this.filePathInfo = filePathInfo;
        this.sharedStatus = sharedStatus;
    }

    @Override
    public KMessage pop(KOptions options) throws Exception {
        File file = filePathInfo.pop();
        if(file == null) {
            return null;
        }

        byte[] body = Files.readAllBytes(file.toPath());
        if(sharedStatus != null) {
            sharedStatus.incrementRead();
        }
        AMQP.BasicProperties props = null;
        if(!options.bodyOnly()) {
            props = CustomObjectMapper.om.readValue(new File(file.getPath() + Constant.FILE_PROPERTIES_SUFFIX), AMQP.BasicProperties.class);
        }
        long deliveryTag = Constant.extractDeliveryTagFromKey(file.getName());
        return new KMessage(props, body, deliveryTag);
    }

    @Override
    public void close() throws Exception {

    }
}
