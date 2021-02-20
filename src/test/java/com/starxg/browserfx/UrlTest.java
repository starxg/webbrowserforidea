package com.starxg.browserfx;

import com.shopobot.util.URL;
import org.junit.Test;

public class UrlTest {
    @Test
    public void test() {
        URL url = URL.get("www.baidu.com");
        System.out.println(url.toJavaURL());

        url = URL.get("www.baidu.com:443");
        System.out.println(url.toJavaURL());
    }
}
