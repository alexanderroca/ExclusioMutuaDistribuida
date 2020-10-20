public class Log {

    public static final boolean logStatus = true;

    public static void logMessage(String message){

        if (logStatus)
            System.out.println(message);

    }

}
