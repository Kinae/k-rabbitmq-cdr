package eu.kinae.k_rabbitmq_cdr.protocol;

public interface Target extends Component {

    boolean run() throws Exception;

}