package burp_injector.util;

import burp.api.montoya.logging.Logging;
import burp_injector.config.DebugConfig;

/**
 * A utility class for logging
 */
public class Logger {
    private static Logging logger = null;

    public static void log(String status, String message) {
        if (logger != null) {
            switch (status) {
                case "ERROR":
                    logger.raiseErrorEvent(message);
                    break;
                case "WARN":
                case "INFO":
                    logger.raiseInfoEvent(message);
                    break;
                case "DEBUG":
                    if (DebugConfig.getInstance().isDebugEnabled()) {
                        logger.raiseDebugEvent(message);
                    }
            }
            
        } else {
            System.out.println(String.format("[%s] %s", status, message));
        }
    }

    public static void setLogger(Logging logger) {
        Logger.logger = logger;
    }
}
