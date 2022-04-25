package eu.kinae.k_rabbitmq_cdr.utils;

import java.nio.file.Path;

public final class Constant {

    public static final String PROJECT_NAME = "k-rabbitmq-cdr";
    public static final long TIMESTAMP = System.currentTimeMillis();
    public static final String TMPDIR = System.getProperty("java.io.tmpdir");
    public static final String UNDERSCORE = "_";

    public static final Path PROJECT_TMPDIR = Path.of(Constant.TMPDIR, Constant.PROJECT_NAME + Constant.UNDERSCORE + Constant.TIMESTAMP);

    public static final String FILE_PROPERTIES_SUFFIX = "_props.json";
    public static final String FILE_PROPERTIES_PREFIX = PROJECT_NAME + UNDERSCORE;

    private Constant() {
    }
}
