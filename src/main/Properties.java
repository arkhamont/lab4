package main;

import java.io.InputStream;

public class Properties {
    public static final String HOST = "host";
    public static final String LOG_FILE = "log_file";

    private static final java.util.Properties properties = new java.util.Properties();

    public static String getProperty(String name) {
        if (properties.isEmpty()){
            try (InputStream is = Properties.class.getClassLoader().getResourceAsStream("server.properties")) {
                properties.load(is);
            } catch (Exception ex){
                throw new RuntimeException("Error reading database settings file.");
            }
        }
        return properties.getProperty(name);
    }
}