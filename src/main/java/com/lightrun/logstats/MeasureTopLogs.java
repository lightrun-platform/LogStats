package com.lightrun.logstats;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class MeasureTopLogs {
    private static final String BLANK_SPACE = "                                        ";
    private static final int CHARACTER_COUNT = BLANK_SPACE.length();
    private final ReentrantLock lock = new ReentrantLock();
    private Map<String,Long> logs = new HashMap<>();
    private static final String loggerName = MeasureTopLogs.class.getName();
    private final Logger logger = Logger.getLogger(loggerName);

    private MeasureTopLogs(long statsFrequency) {
        Logger.getLogger("").addHandler(new CustomHandler());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                clearAndPrint();
            }
        }, statsFrequency, statsFrequency);
    }

    public static void install(long statsFrequency) {
        new MeasureTopLogs(statsFrequency);
    }

    private void increment(String message) {
        try {
            lock.lock();
            long newVal = logs.getOrDefault(message, 0L) + 1;
            logs.put(message, newVal);
        } finally {
            lock.unlock();
        }
    }

    private void clearAndPrint() {
        Map<String, Long> oldLogs;
        try {
            lock.lock();
            if(logs.isEmpty()) {
                return;
            }
            oldLogs = logs;
            logs = new HashMap<>();
        } finally {
            lock.unlock();
        }

        StringBuilder output = new StringBuilder("\n")
                .append(xCharacters("Message", CHARACTER_COUNT))
                .append(" | Frequency\n")
                .append("===========================================================\n");
        oldLogs.entrySet().stream()
                .sorted(Comparator.comparingLong(Map.Entry::getValue))
                .forEach(e -> {
                    output.append(xCharacters(e.getKey(), CHARACTER_COUNT));
                    output.append(" | ");
                    output.append(e.getValue());
                    output.append("\n");
                });
        logger.log(Level.INFO, output.toString());
    }

    private String xCharacters(String s, int x) {
        if(s.length() == x) {
            return s;
        }
        if(s.length() > x) {
            return s.substring(0, x);
        }
        return s + BLANK_SPACE.substring(0, x - s.length());
    }

    private class CustomHandler extends Handler {
        @Override
        public void publish(LogRecord record) {
            if(record.getLoggerName() != loggerName) {
                increment(record.getMessage());
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }
    }
}
