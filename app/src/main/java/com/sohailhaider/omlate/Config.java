package com.sohailhaider.omlate;

/**
 * Created by Sohail Haider on 09-Dec-16.
 */
public class Config {
    public static String WebHostIP = "192.168.10.7";
    public static String WebHostPort = "3825";
    public static String WebHostPublicSub = "omlate";

    public static String Red5ServerIP = "192.168.10.7";
    public static String Red5Port = "1935";

    public static String Red5PublicSub = "omlate";

    public static String getWebLink() {
        return "http://" + WebHostIP + ":" + WebHostPort + "/" +WebHostPublicSub + "/";
    }
}
