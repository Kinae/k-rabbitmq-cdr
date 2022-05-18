package eu.kinae.k_rabbitmq_cdr.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgressDisplayPrinter implements Runnable {

    private volatile boolean printRead = true;
    private volatile boolean printWrite = true;

    private static final String READING = "reading";
    private static final String WRITING = "writing";
    private static final String PROGRESS_SYMBOL = "#";
    private static final String PROGRESS_BAR = ".........................";
    private static final String DISPLAY_FORMAT = "%s [%s] %d/%d (%.2f%%)";
    private static final Logger logger = LoggerFactory.getLogger(ProgressDisplayPrinter.class);

    private final SharedStatus sharedStatus;

    public ProgressDisplayPrinter(SharedStatus sharedStatus) {
        this.sharedStatus = sharedStatus;
    }

    @Override
    public void run() {
        if(sharedStatus.getTotal() != 0) {
            long write = sharedStatus.getWrite();
            long read = sharedStatus.getRead();

            if(read != 0 && printRead) {
                if(read == sharedStatus.getTotal()) {
                    printLastReadProgress();
                } else {
                    printReadProgress(read);
                }
            }

            if(write != 0 && printWrite) {
                if(write == sharedStatus.getTotal()) {
                    printLastWriteProgress();
                } else {
                    printWriteProgress(write);
                }
            }

            if(read != 0 && write != 0) {
                printEmptyLine();
            }
        }
    }

    public void printReadProgress() {
        printReadProgress(sharedStatus.getRead());
    }

    public void printReadProgress(long read) {
        printProgress(READING, read);
    }

    public void printLastReadProgress() {
        if(printRead) {
            printReadProgress();
            printRead = false;
        }
    }

    public void printWriteProgress() {
        printWriteProgress(sharedStatus.getWrite());
    }

    public void printWriteProgress(long write) {
        printProgress(WRITING, write);
    }

    private void printProgress(String type, long value) {
        double current = 25 * (double) value / (double) sharedStatus.getTotal();
        String str = PROGRESS_SYMBOL.repeat((int) current);
        String progress = PROGRESS_BAR.substring(str.length());
        logger.info(String.format(DISPLAY_FORMAT, type, str + progress, value, sharedStatus.getTotal(), current * 4));
    }

    public void printLastWriteProgress() {
        if(printWrite) {
            printWriteProgress();
            printWrite = false;
        }
    }

    public void printEmptyLine() {
        logger.info("");
    }
}
