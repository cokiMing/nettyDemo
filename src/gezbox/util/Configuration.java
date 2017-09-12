package gezbox.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wuyiming on 2017/9/7.
 */
public class Configuration {
    private static Properties properties = new Properties();
    private static Configuration config = null;

    public Configuration() {
    }

    public Configuration getInstance() {
        return config;
    }

    public static void appendProperties(InputStream is) throws IOException {
        properties.load(is);
    }

    public static void appendProperties(Properties prop) throws IOException {
        Iterator var1 = prop.entrySet().iterator();

        while(var1.hasNext()) {
            Map.Entry<Object, Object> entry = (Map.Entry)var1.next();
            properties.put(entry.getKey(), entry.getValue());
        }

    }

    public static String getString(String key, String defaultValue) {
        String value = (String)properties.get(key);
        if(value == null) {
            value = defaultValue;
        }

        if(value != null && !"".equals(value)) {
            return value;
        } else {
            throw new RuntimeException("No config properties: " + key);
        }
    }

    public static String getString(String key) {
        return getString(key, "");
    }

    public static String getStringWithoutErr(String key) {
        return (String)properties.get(key);
    }

    public static String getStringWithoutErr(String key, String defaultValue) {
        String result = (String)properties.get(key);
        if(result == null) {
            result = defaultValue;
        }

        return result;
    }

    public static boolean isDevMode() {
        Boolean b = (Boolean)properties.get("devMode");
        return b == null?false:b.booleanValue();
    }

    private static InputStream loadFromClassPath(String path) {
        InputStream is = null;
        if(is == null) {
            try {
                is = new FileInputStream(path);
                System.out.println("读取到文件cfg.properties");
            } catch (FileNotFoundException var3) {
                System.out.println("cfg.properties文件读取错误" + var3.getMessage());
                is = null;
            }
        }

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if(is == null) {
            is = loader.getResourceAsStream(path);
        }

        if(is == null) {
            loader = Configuration.class.getClassLoader();
            is = loader.getResourceAsStream(path);
        }

        if(is == null) {
            loader = ClassLoader.getSystemClassLoader();
            is = loader.getResourceAsStream(path);
        }

        return (InputStream)is;
    }

    static {
        try {
            properties.load(loadFromClassPath("cfg.properties"));
            config = new Configuration();
        } catch (IOException var1) {
            config = null;
        }

    }
}
