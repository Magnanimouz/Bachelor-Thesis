import java.io.*;
import java.util.logging.*;

public class Main {

    public static void main(String[] args) throws Exception{
        setLogger();

        UserActionHandler handler = new UserActionHandler();
        handler.showOptions();

        System.exit(0);
    }

    private static void setLogger() throws Exception{
        LogManager logManager = LogManager.getLogManager();
        logManager.reset();

        Handler fileHandler = new FileHandler("./out/Logs.log", 10000, 3, true);
        fileHandler.setFormatter(new SimpleFormatter());
        Logger.getLogger("").addHandler(fileHandler);

        Logger logger;
        LoggingOutputStream los;

        logger = Logger.getLogger("stderr");
        los= new LoggingOutputStream(logger, StdErrLevel.STDERR);
        System.setErr(new PrintStream(los, true));
    }

}