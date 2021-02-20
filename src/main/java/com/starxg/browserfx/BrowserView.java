package com.starxg.browserfx;

import javafx.util.Callback;

import javax.swing.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

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
    void onUrlChange(Consumer<String> consumer);

    /**
     * alert
     * 
     * @param consumer
     *            consumer
     */
    void onAlert(Consumer<String> consumer);

    /**
     * confirm
     * 
     * @param callback
     *            callback
     */
    void onConfirm(Callback<String, Boolean> callback);

    /**
     * prompt
     * 
     * @param callback
     *            callback
     */
    void onPrompt(BiFunction<String, String, String> callback);

    /**
     * 进度条改变通知
     * 
     * @param consumer
     *            consumer
     */
    void onProgressChange(Consumer<Double> consumer);

    /**
     * 后退
     */
    void back();

    /**
     * 前进
     */
    void forward();

    /**
     * 是否可以后退
     * 
     * @return true：可以
     */
    boolean canBack();

    /**
     * 是否可以前进
     * 
     * @return true：可以
     */
    boolean canForward();

}
