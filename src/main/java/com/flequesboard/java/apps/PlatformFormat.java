package com.flequesboard.java.apps;
import java.util.*;

public class PlatformFormat {
    private Map<String, String> DEVICES;
    private Map<String, String> BROWSERS;
    private Map<String, String> OSES;

    PlatformFormat() {
        DEVICES = new HashMap<>();
        BROWSERS = new HashMap<>();
        OSES = new HashMap<>();

        //OSES
        OSES.put("Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML- like Gecko) Version/4.0 Mobile Safari/534.30", "Android");
        OSES.put("Mozilla/5.0 (Linux; U; Android 2.3.6; en-us; Nexus S Build/GRK39F) AppleWebKit/533.1 (KHTML- like Gecko) Version/4.0 Mobile Safari/533.1", "Android");
        OSES.put("Mozilla/5.0 (BB10; Touch) AppleWebKit/537.1+ (KHTML- like Gecko) Version/10.0.0.1337 Mobile Safari/537.1+", "BlackBerry");
        OSES.put("Mozilla/5.0 (PlayBook; U; RIM Tablet OS 2.1.0; en-US) AppleWebKit/536.2+ (KHTML- like Gecko) Version/7.2.1.0 Safari/536.2+", "BlackBerry");
        OSES.put("Mozilla/5.0 (BlackBerry; U; BlackBerry 9900; en-US) AppleWebKit/534.11+ (KHTML- like Gecko) Version/7.0.0.187 Mobile Safari/534.11+", "BlackBerry");
        OSES.put("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML- like Gecko) Chrome/31.0.1650.63 Safari/537.36", "Mac");
        OSES.put("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML- like Gecko) Chrome/31.0.1650.16 Safari/537.36", "Windows");
        OSES.put("Mozilla/5.0 (Linux; Android 4.1.2; Nexus 7 Build/JZ054K) AppleWebKit/535.19 (KHTML-like Gecko) Chrome/18.0.1025.166 Safari/535.19", "Android Tablet");
        OSES.put("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML- like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19", "Android Mobile");
        OSES.put("Mozilla/5.0 (iPad; CPU OS 7_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML- like Gecko) " +
                "CriOS/30.0.1599.12 Mobile/11A465 Safari/8536.25", "iPad - iOS 7");
        OSES.put("Mozilla/5.0 (iPhone; CPU iPhone OS 7_0_2 like Mac OS X) AppleWebKit/537.51.1 (KHTML- like Gecko) " +
                "CriOS/30.0.1599.12 Mobile/11A501 Safari/8536.25", "iPhone - iOS 7");
        OSES.put("Mozilla/5.0 (Android; Mobile; rv:14.0) Gecko/14.0 Firefox/14.0", "Android Mobile");
        OSES.put("Mozilla/5.0 (Android; Tablet; rv:14.0) Gecko/14.0 Firefox/14.0", "Android Tablet");
        OSES.put("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0.1) Gecko/20100101 Firefox/4.0.1", "Mac X");
        OSES.put("Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1", "Windows");
        OSES.put("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:7.0.1) Gecko/20100101 Firefox/7.0.1", "Mac X");
        OSES.put("Mozilla/5.0 (Windows NT 6.1; Intel Mac OS X 10.6; rv:7.0.1) Gecko/20100101 Firefox/7.0.1", "Windows");
        OSES.put("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)", "Windows");
        OSES.put("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)", "Windows");
        OSES.put("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)", "Windows");
        OSES.put("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)", "Windows");
        OSES.put("Mozilla/5.0 (iPad; CPU OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML- like Gecko) Version/8.0 " +
                "Mobile/12A4345d Safari/600.1.4", "iPad - iOS 8");
        OSES.put("Mozilla/5.0 (iPad; CPU OS 7_0_2 like Mac OS X) AppleWebKit/537.51.1 (KHTML- like Gecko) Version/7.0 Mobile/11A501 Safari/9537.53", "iPad - iOS 7");
        OSES.put("Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML- like Gecko)Version/6.0 " +
                "Mobile/10A5376e Safari/8536.25", "iPad - iOS 6");
        OSES.put("Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML- like Gecko) " +
                "Version/8.0 Mobile/12A4345d Safari/600.1.4", "iPhone - iOS 8");
        OSES.put("Mozilla/5.0 (iPhone; CPU iPhone OS 7_0_2 like Mac OS X) AppleWebKit/537.51.1 (KHTML- like Gecko) Version/7.0 Mobile/11A4449d Safari/9537.53", "iPhone - iOS 7");
        OSES.put("Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML- like Gecko) " +
                "Version/6.0 Mobile/10A5376e Safari/8536.25", "iPhone - iOS 6");
        OSES.put("Mozilla/5.0 (MeeGo; NokiaN9) AppleWebKit/534.13 (KHTML- like Gecko) NokiaBrowser/8.5.0 Mobile " +
                "Safari/534.13", "Android");
        OSES.put("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML- like Gecko) " +
                "Chrome/31.0.1650.63 Safari/537.36 OPR/18.0.1284.68", "Mac X");
        OSES.put("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML- like Gecko) Chrome/31.0.1650.63 Safari/537.36 OPR/18.0.1284.68", "Windows");
        OSES.put("Opera/9.80 (Macintosh; Intel Mac OS X 10.9.1) Presto/2.12.388 Version/12.16", "Mac X");
        OSES.put("Opera/9.80 (Windows NT 6.1) Presto/2.12.388 Version/12.16", "Windows");
        OSES.put("Mozilla/5.0 (Linux; U; en-us; KFTHWI Build/JDQ39) AppleWebKit/535.19 (KHTML- like Gecko) Silk/3.13 Safari/535.19 Silk-Accelerated=true", "Kindle Fire (Desktop view)");
        OSES.put("Mozilla/5.0 (Linux; U; Android 4.2.2; en-us; KFTHWI Build/JDQ39) AppleWebKit/535.19(KHTML- like Gecko) Silk/3.13 Mobile Safari/535.19 Silk-Accelerated=true", "Kindle Fire (Mobile view)");

        DEVICES.put("Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 " +
                "(KHTML- like Gecko) Version/4.0 Mobile Safari/534.30", "Tablet");
        DEVICES.put("Mozilla/5.0 (Linux; U; Android 2.3.6; en-us; Nexus S Build/GRK39F) AppleWebKit/533.1 (KHTML- " +
                "like Gecko) Version/4.0 Mobile Safari/533.1", "Tablet");
        DEVICES.put("Mozilla/5.0 (BB10; Touch) AppleWebKit/537.1+ (KHTML- like Gecko) Version/10.0.0.1337 Mobile " +
                "Safari/537.1+", "Mobile");
        DEVICES.put("Mozilla/5.0 (PlayBook; U; RIM Tablet OS 2.1.0; en-US) AppleWebKit/536.2+ (KHTML-like Gecko) " +
                "Version/7.2.1.0 Safari/536.2+", "Mobile");
        DEVICES.put("Mozilla/5.0 (BlackBerry; U; BlackBerry 9900; en-US) AppleWebKit/534.11+ (KHTML- like Gecko) " +
                "Version/7.0.0.187 Mobile Safari/534.11+", "Mobile");
        DEVICES.put("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML- like Gecko) " +
                "Chrome/31.0.1650.63 Safari/537.36", "Mac");
        DEVICES.put("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML- like Gecko) Chrome/31.0.1650.16 " +
                "Safari/537.36", "PC");
        DEVICES.put("Mozilla/5.0 (Linux; Android 4.1.2; Nexus 7 Build/JZ054K) AppleWebKit/535.19 (KHTML- like Gecko) " +
                "Chrome/18.0.1025.166 Safari/535.19", "Tablet");
        DEVICES.put("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19(KHTML- like " +
                "Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19", "Mobile");
        DEVICES.put("Mozilla/5.0 (iPad; CPU OS 7_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML- like Gecko) CriOS/30.0.1599.12 Mobile/11A465 Safari/8536.25", "Tablet");
        DEVICES.put("Mozilla/5.0 (iPhone; CPU iPhone OS 7_0_2 like Mac OS X) AppleWebKit/537.51.1 (KHTML- like Gecko) CriOS/30.0.1599.12 Mobile/11A501 Safari/8536.25", "Tablet");
        DEVICES.put("Mozilla/5.0 (Android; Mobile; rv:14.0) Gecko/14.0 Firefox/14.0", "Mobile");
        DEVICES.put("Mozilla/5.0 (Android; Tablet; rv:14.0) Gecko/14.0 Firefox/14.0", "Tablet");
        DEVICES.put("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0.1) Gecko/20100101 Firefox/4.0.1", "Mac");
        DEVICES.put("Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1", "PC");
        DEVICES.put("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:7.0.1) Gecko/20100101 Firefox/7.0.1", "Mac");
        DEVICES.put("Mozilla/5.0 (Windows NT 6.1; Intel Mac OS X 10.6; rv:7.0.1) Gecko/20100101 Firefox/7.0.1", "PC");
        DEVICES.put("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)", "PC");
        DEVICES.put("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)", "PC");
        DEVICES.put("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)", "PC");
        DEVICES.put("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)", "PC");
        DEVICES.put("Mozilla/5.0 (iPad; CPU OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML- like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4", "Tablet");
        DEVICES.put("Mozilla/5.0 (iPad; CPU OS 7_0_2 like Mac OS X) AppleWebKit/537.51.1 (KHTML- likeGecko) Version/7.0 Mobile/11A501 Safari/9537.53", "Tablet");
        DEVICES.put("Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML- like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25", "Tablet");
        DEVICES.put("Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML- like Gecko) " +
                "Version/8.0 Mobile/12A4345d Safari/600.1.4", "Mobile");
        DEVICES.put("Mozilla/5.0 (iPhone; CPU iPhone OS 7_0_2 like Mac OS X) AppleWebKit/537.51.1 (KHTML- like Gecko)" +
                " Version/7.0 Mobile/11A4449d Safari/9537.53", "Mobile");
        DEVICES.put("Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML-like Gecko) " +
                "Version/6.0 Mobile/10A5376e Safari/8536.25", "Mobile");
        DEVICES.put("Mozilla/5.0 (MeeGo; NokiaN9) AppleWebKit/534.13 (KHTML- like Gecko) NokiaBrowser/8.5.0 Mobile " +
                "Safari/534.13", "Mobile");
        DEVICES.put("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML- like Gecko) Chrome/31.0.1650.63 Safari/537.36 OPR/18.0.1284.68", "Mac");
        DEVICES.put("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML- like Gecko) Chrome/31.0.1650.63 " +
                "Safari/537.36 OPR/18.0.1284.68", "PC");
        DEVICES.put("Opera/9.80 (Macintosh; Intel Mac OS X 10.9.1) Presto/2.12.388 Version/12.16", "Mac");
        DEVICES.put("Opera/9.80 (Windows NT 6.1) Presto/2.12.388 Version/12.16", "PC");
        DEVICES.put("Mozilla/5.0 (Linux; U; en-us; KFTHWI Build/JDQ39) AppleWebKit/535.19 (KHTML- like Gecko) " +
                "Silk/3.13 Safari/535.19 Silk-Accelerated=true", "Kindle");
        DEVICES.put("Mozilla/5.0 (Linux; U; Android 4.2.2; en-us; KFTHWI Build/JDQ39) AppleWebKit/535.19 (KHTML- like" +
                " Gecko) Silk/3.13 Mobile Safari/535.19 Silk-Accelerated=true", "Kindle");

        BROWSERS.put("Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML- like Gecko) Version/4.0 Mobile Safari/534.30", "Android");
        BROWSERS.put("Mozilla/5.0 (Linux; U; Android 2.3.6; en-us; Nexus S Build/GRK39F) AppleWebKit/533.1 (KHTML- like Gecko) Version/4.0 Mobile Safari/533.1", "Android");
        BROWSERS.put("Mozilla/5.0 (BB10; Touch) AppleWebKit/537.1+ (KHTML- like Gecko) Version/10.0.0.1337 Mobile Safari/537.1+", "BlackBerry");
        BROWSERS.put("Mozilla/5.0 (PlayBook; U; RIM Tablet OS 2.1.0; en-US) AppleWebKit/536.2+ (KHTML- like Gecko) Version/7.2.1.0 Safari/536.2+", "BlackBerry");
        BROWSERS.put("Mozilla/5.0 (BlackBerry; U; BlackBerry 9900; en-US) AppleWebKit/534.11+ (KHTML-like Gecko) Version/7.0.0.187 Mobile Safari/534.11+", "BlackBerry");
        BROWSERS.put("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML- like Gecko) Chrome/31.0.1650.63 Safari/537.36", "Chrome");
        BROWSERS.put("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML- like Gecko) Chrome/31.0.1650.16 Safari/537.36", "Chrome");
        BROWSERS.put("Mozilla/5.0 (Linux; Android 4.1.2; Nexus 7 Build/JZ054K) AppleWebKit/535.19 (KHTML- like Gecko) Chrome/18.0.1025.166 Safari/535.19", "Chrome");
        BROWSERS.put("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML- like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19", "Chrome");
        BROWSERS.put("Mozilla/5.0 (iPad; CPU OS 7_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML- like Gecko) CriOS/30.0.1599.12 Mobile/11A465 Safari/8536.25", "Chrome");
        BROWSERS.put("Mozilla/5.0 (iPhone; CPU iPhone OS 7_0_2 like Mac OS X) AppleWebKit/537.51.1 (KHTML- like Gecko) CriOS/30.0.1599.12 Mobile/11A501 Safari/8536.25", "Chrome");
        BROWSERS.put("Mozilla/5.0 (Android; Mobile; rv:14.0) Gecko/14.0 Firefox/14.0", "Firefox");
        BROWSERS.put("Mozilla/5.0 (Android; Tablet; rv:14.0) Gecko/14.0 Firefox/14.0", "Firefox");
        BROWSERS.put("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0.1) Gecko/20100101 Firefox/4.0.1", "Firefox");
        BROWSERS.put("Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1", "Firefox");
        BROWSERS.put("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:7.0.1) Gecko/20100101 Firefox/7.0.1", "Firefox");
        BROWSERS.put("Mozilla/5.0 (Windows NT 6.1; Intel Mac OS X 10.6; rv:7.0.1) Gecko/20100101 Firefox/7.0.1", "Firefox");
        BROWSERS.put("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)", "Internet Explorer 10");
        BROWSERS.put("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)", "Internet Explorer 7");
        BROWSERS.put("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)", "Internet Explorer 8");
        BROWSERS.put("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)", "Internet Explorer 9");
        BROWSERS.put("Mozilla/5.0 (iPad; CPU OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML- like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4", "Safari");
        BROWSERS.put("Mozilla/5.0 (iPad; CPU OS 7_0_2 like Mac OS X) AppleWebKit/537.51.1 (KHTML- like Gecko) Version/7.0 Mobile/11A501 Safari/9537.53", "Safari");
        BROWSERS.put("Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML- like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25", "Safari");
        BROWSERS.put("Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML- like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4", "Safari");
        BROWSERS.put("Mozilla/5.0 (iPhone; CPU iPhone OS 7_0_2 like Mac OS X) AppleWebKit/537.51.1 (KHTML- like Gecko) Version/7.0 Mobile/11A4449d Safari/9537.53", "Safari");
        BROWSERS.put("Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML- like Gecko) " +
                "Version/6.0 Mobile/10A5376e Safari/8536.25", "Safari");
        BROWSERS.put("Mozilla/5.0 (MeeGo; NokiaN9) AppleWebKit/534.13 (KHTML- like Gecko) NokiaBrowser/8.5.0 Mobile Safari/534.13", "MeeGo");
        BROWSERS.put("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML- like Gecko) Chrome/31.0.1650.63 Safari/537.36 OPR/18.0.1284.68", "Opera");
        BROWSERS.put("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML- like Gecko) Chrome/31.0.1650.63 Safari/537.36 OPR/18.0.1284.68", "Opera");
        BROWSERS.put("Opera/9.80 (Macintosh; Intel Mac OS X 10.9.1) Presto/2.12.388 Version/12.16", "Opera");
        BROWSERS.put("Opera/9.80 (Windows NT 6.1) Presto/2.12.388 Version/12.16", "Opera");
        BROWSERS.put("Mozilla/5.0 (Linux; U; en-us; KFTHWI Build/JDQ39) AppleWebKit/535.19 (KHTML- like Gecko) Silk/3.13 Safari/535.19 Silk-Accelerated=true", "Silk");
        BROWSERS.put("Mozilla/5.0 (Linux; U; Android 4.2.2; en-us; KFTHWI Build/JDQ39) AppleWebKit/535.19 (KHTML- like Gecko) Silk/3.13 Mobile Safari/535.19 Silk-Accelerated=true", "Silk");
    }
    public String getDevice(String key){
        String device = DEVICES.get(key);
        return (device==null)?"unknown":device;
    }

    public String getOS(String key) {
        String os = OSES.get(key);
        return (os==null)?"unknown":os;
    }

    public String getBrowser(String key) {
        String browser = BROWSERS.get(key);
        return (browser==null)?"generic":browser;
    }
}


