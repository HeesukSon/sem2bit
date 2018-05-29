package heesuk.percom.sem2bit;

/**
 * Created by root on 31/10/2016.
 */
public class ProbeLogger {
    private static StringBuilder log = new StringBuilder();
    private static StringBuilder err = new StringBuilder();
    private static StringBuilder stat = new StringBuilder();

    public static synchronized void appendLog(String tag, String logStr){
        if(!ConfigUtil.getInstance().log_mode.equals("none")) {
            if (ConfigUtil.getInstance().log_mode.equals("both")) {
                //System.out.print(logStr);
                log.append(logStr);
            } else if (ConfigUtil.getInstance().log_mode.equals(tag)) {
                //System.out.print(logStr);
                log.append(logStr);
            }
        }
    }

    public static synchronized void appendLogln(String tag, String logStr){
        appendLog(tag, logStr+"\n");
    }

    public static synchronized void appendErr(String tag, String errStr){
        if(!ConfigUtil.getInstance().log_mode.equals("none")) {
            if (ConfigUtil.getInstance().log_mode.equals("both")) {
                //System.err.print(errStr);
                err.append(errStr);
            } else if (ConfigUtil.getInstance().log_mode.equals(tag)) {
                //System.err.print(errStr);
                err.append(errStr);
            }
        }
    }

    public static synchronized void appendErrln(String tag, String errStr){
        appendErr(tag, errStr+"\n");
    }

    public static synchronized void appendStat(String statStr){
        stat.append(statStr);
    }

    public static synchronized void appendStatln(String statStr){
        stat.append(statStr+"\n");
    }

    public static String getLog(){
        return log.toString();
    }

    public static String getErr(){
        return err.toString();
    }

    public static String getStat(){
        return stat.toString();
    }

    public static void printLog(){
        System.out.println("##### ProbeLogger.printLog() #####");
        System.out.println(getLog());
    }

    public static void printErr(){
        System.out.println(getErr());
    }

    public static void printStat(){
        System.out.println(getStat());
    }
}
