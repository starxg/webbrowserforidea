package com.starxg.browserfx;

import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.*;

import org.apache.commons.lang3.RandomUtils;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.cef.handler.CefLifeSpanHandlerAdapter;
import org.cef.handler.CefLoadHandlerAdapter;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefClient;

/**
 * Jcef Browser
 * 
 * @author huangxingguang@lvmama.com
 * @date 2021-02-21 12:11
 */
public class JcefBrowser implements BrowserView, Disposable {

    private JBCefBrowser browser;
    private CefBrowser cefBrowser;
    private CefClient cefClient;
    private Consumer<String> urlChangedConsumer;
    private Consumer<Double> progressChangedConsumer;

    JcefBrowser() {
        browser = new JBCefBrowser("about:blank");
        cefBrowser = browser.getCefBrowser();
        cefClient = browser.getJBCefClient().getCefClient();
        JBCefClient jbCefClient = browser.getJBCefClient();

        jbCefClient.addLifeSpanHandler(new CefLifeSpanHandlerAdapter() {
            @Override
            public boolean onBeforePopup(CefBrowser browser, CefFrame frame, String targetUrl, String targetFrameName) {
                load(targetUrl);
                return true;
            }
        }, cefBrowser);

        jbCefClient.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public void onAddressChange(CefBrowser browser, CefFrame frame, String url) {
                if (Objects.nonNull(urlChangedConsumer)) {
                    urlChangedConsumer.accept(url);
                }
            }
        }, cefBrowser);

        jbCefClient.addLoadHandler(new CefLoadHandlerAdapter() {
            private volatile double progress = 0.0;

            @Override
            public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack,
                    boolean canGoForward) {
                super.onLoadingStateChange(browser, isLoading, canGoBack, canGoForward);
                if (Objects.nonNull(progressChangedConsumer)) {
                    synchronized (this) {
                        if (isLoading) {
                            if (progress == 0) {
                                progress = RandomUtils.nextDouble(0.1, 0.8);
                            }
                        } else {
                            progress = 0;
                        }
                        progressChangedConsumer.accept(progress);
                    }
                }
            }

            @Override
            public void onLoadError(CefBrowser browser, CefFrame frame, ErrorCode errorCode, String errorText, String failedUrl) {
                if (Objects.nonNull(progressChangedConsumer)) {
                    synchronized (this) {

                            progress = 0;
                        progressChangedConsumer.accept(progress);
                    }
                }
            }

            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                if (Objects.nonNull(progressChangedConsumer)) {
                    synchronized (this) {

                        progress = 0;
                        progressChangedConsumer.accept(progress);
                    }
                }
            }
        }, cefBrowser);

    }

    @Override
    public JComponent getBrowser() {
        return browser.getComponent();
    }

    @Override
    public void load(String url) {
        browser.loadURL(url);
    }

    @Override
    public void onUrlChange(Consumer<String> consumer) {
        urlChangedConsumer = Objects.requireNonNull(consumer, "consumer");
    }

    @Override
    public void onProgressChange(Consumer<Double> consumer) {
        progressChangedConsumer = Objects.requireNonNull(consumer, "consumer");
    }

    @Override
    public void back() {
        if (canBack()) {
            cefBrowser.goBack();
        }
    }

    @Override
    public void forward() {
        if (canForward()) {
            cefBrowser.goForward();
        }
    }

    @Override
    public boolean canBack() {
        return cefBrowser.canGoBack();
    }

    @Override
    public boolean canForward() {
        return cefBrowser.canGoForward();
    }

    @Override
    public void openDevTools() {
        browser.openDevtools();
    }

    @Override
    public void executeScript(String script) {
        cefBrowser.executeJavaScript(script, null, 0);
    }

    @Override
    public Type type() {
        return Type.JCEF;
    }

    @Override
    public void dispose() {
        cefClient.removeLoadHandler();
        cefBrowser.stopLoad();
        cefBrowser.close(false);
        Disposer.dispose(browser);
    }
}
