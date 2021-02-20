package com.starxg.browserfx;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.swing.*;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.util.Callback;

/**
 * javafx浏览器
 * 
 * @author huangxingguang
 * @date 2019-04-21 13:52
 */
class JavaFxBrowser implements BrowserView {
    private WebView browser;
    private WebEngine webEngine;
    private JFXPanel jfxPanel;
    private Consumer<Double> progressChangedConsumer;
    private Consumer<String> urlChangedConsumer;
    private Consumer<String> alertConsumer;
    private Callback<String, Boolean> confirmCallback;
    private BiFunction<String, String, String> promptCallback;

    JavaFxBrowser() {
        jfxPanel = new JFXPanel();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            browser = getWebView();
            webEngine = browser.getEngine();
            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(browser);
            Scene scene = new Scene(borderPane);
            jfxPanel.setScene(scene);
            latch.countDown();
        });

        try {
            latch.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JComponent getBrowser() {
        return jfxPanel;
    }

    @Override
    public void load(String url) {
        Platform.runLater(() -> webEngine.load(url));
    }

    @Override
    public void onUrlChange(Consumer<String> consumer) {
        urlChangedConsumer = Objects.requireNonNull(consumer, "consumer");
    }

    @Override
    public void onAlert(Consumer<String> consumer) {
        alertConsumer = Objects.requireNonNull(consumer, "consumer");
    }

    @Override
    public void onConfirm(Callback<String, Boolean> callback) {
        confirmCallback = Objects.requireNonNull(callback, "callback");
    }

    @Override
    public void onPrompt(BiFunction<String, String, String> callback) {
        promptCallback = Objects.requireNonNull(callback, "callback");
    }

    @Override
    public void onProgressChange(Consumer<Double> consumer) {
        progressChangedConsumer = Objects.requireNonNull(consumer, "consumer");
    }

    @Override
    public void back() {
        // https://stackoverflow.com/questions/18928333/how-to-program-back-and-forward-buttons-in-javafx-with-webview-and-webengine
        final WebHistory history = webEngine.getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        int currentIndex = history.getCurrentIndex();
        Platform.runLater(() -> history.go(entryList.size() > 1 && currentIndex > 0 ? -1 : 0));
    }

    @Override
    public void forward() {
        // https://stackoverflow.com/questions/18928333/how-to-program-back-and-forward-buttons-in-javafx-with-webview-and-webengine
        final WebHistory history = webEngine.getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        int currentIndex = history.getCurrentIndex();
        Platform.runLater(() -> history.go(entryList.size() > 1 && currentIndex < entryList.size() - 1 ? 1 : 0));
    }

    @Override
    public boolean canBack() {
        final WebHistory history = webEngine.getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        return entryList.size() > 1 && history.getCurrentIndex() < entryList.size() - 1;
    }

    @Override
    public boolean canForward() {
        final WebHistory history = webEngine.getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        return entryList.size() > 1 && history.getCurrentIndex() > 0;
    }

    private WebView getWebView() {
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalThreadStateException();
        }
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.getLoadWorker().progressProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(progressChangedConsumer)) {
                progressChangedConsumer.accept(newValue.doubleValue());
            }

        });

        webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(urlChangedConsumer)) {
                urlChangedConsumer.accept(webEngine.getLocation());
            }
        });

        webEngine.setOnAlert(event -> {
            if (Objects.nonNull(alertConsumer)) {
                alertConsumer.accept(event.getData());
            }
        });

        webEngine.setConfirmHandler(param -> {
            if (Objects.isNull(confirmCallback)) {
                return null;
            }
            return confirmCallback.call(param);
        });

        webEngine.setPromptHandler(e -> {
            if (Objects.isNull(promptCallback)) {
                return null;
            }
            return promptCallback.apply(e.getMessage(), e.getDefaultValue());
        });

        return webView;
    }
}
