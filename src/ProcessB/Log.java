package ProcessB;

public class Log {

    public static final boolean logStatus = true;

    public static void logMessage(String message, String messageType, String context, String weight){

        if (logStatus){
            System.out.println(messageType + ": " + context + "->" + weight + ": " + message);
        }

    }

    public static void logMessage(String message, String messageType, String context, String weight, String id){

        if (logStatus){
            System.out.println(messageType + ": " + context + "->" + weight + "->" + id + ": " + message);
        }

    }

}
