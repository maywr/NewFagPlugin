package xyz.maywr.newfagplugin;

import java.util.logging.LogRecord;

public class Filter implements java.util.logging.Filter {

    @Override
    public boolean isLoggable(LogRecord record) {
        if (record.getMessage().contains("Плагин")) {
            return true;
        } else {
            return false;
        }
    }
}
