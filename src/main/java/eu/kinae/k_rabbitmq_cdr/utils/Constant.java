package eu.kinae.k_rabbitmq_cdr.utils;

public final class Constant {

    public static final String PROJECT_NAME = "k-rabbitmq-cdr";
    public static final String FILE_PREFIX = PROJECT_NAME + "_";
    public static final String FILE_PROPERTIES_SUFFIX = ".props";

    private Constant() {
    }

    public static long extractDeliveryTagFromKey(String key) {
        return Long.parseLong(key.substring(Constant.FILE_PREFIX.length()));
    }

    public static long extractDeliveryTagFromKey(String prefix, String key) {
        return Long.parseLong(key.substring(prefix.length() + Constant.FILE_PREFIX.length() + 1));
    }

}
