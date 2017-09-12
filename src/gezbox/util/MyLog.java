package gezbox.util;

/**
 * Created by wuyiming on 2017/9/7.
 */
public class MyLog {

    public static void info(String msg){
        System.out.println(msg);
    }

    public static void debug(String msg){
        System.out.println(msg);
    }

    public static void error(String msg){
        System.out.println(msg);
    }

    public static void error(String msg, Object object){
        System.out.println(msg +":"+object);
    }
}
