package com.starxg.browserfx;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * 浏览器接口
 * 
 * @author huangxingguang
 * @date 2019-04-21 13:58
 */
public interface BrowserView {
    /**
     * 获取浏览器
     * 
     * @return JComponent
     */
    JComponent getBrowser();

    /**
     * 加载新的页面
     * 
     * @param url
     *            地址
     */
    void load(String url);

    /**
     * url改变通知
     * 
     * @param consumer
     *            consumer
     */
    void urlChange(Consumer<String> consumer);

    /**
     * 后退
     */
    void back();

    /**
     * 前进
     */
    void forward();

    /**
     * 是否有下一页
     * 
     * @return true：有
     */
    boolean isNext();

    /**
     * 是否有上一页
     * 
     * @return true：有
     */
    boolean isPrev();

}
