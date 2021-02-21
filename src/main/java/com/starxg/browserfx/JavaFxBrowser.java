package com.starxg.browserfx;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import javax.swing.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.sun.javafx.tk.Toolkit;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

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

    @Override
    public void openDevTools() {
        Document document = webEngine.getDocument();
        if (Objects.isNull(document)) {
            return;
        }

        NodeList heads = document.getElementsByTagName("head");
        if (Objects.isNull(heads) || heads.getLength() == 0) {
            return;
        }

        Node head = heads.item(0);

        Element script = document.createElement("script");
        script.setAttribute("type", "text/javascript");
        script.setAttribute("src", "https://cdn.jsdelivr.net/gh/Tencent/vConsole@3.4.0/dist/vconsole.min.js");

        Element console = document.createElement("script");
        console.setAttribute("type", "text/javascript");
        console.setTextContent("new VConsole()");

        head.appendChild(script);
        head.appendChild(console);
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
            final Object key = new Object();
            ApplicationManager.getApplication().invokeLater(() -> {
                Messages.showMessageDialog(String.valueOf(event.getData()), webEngine.getLocation(), null);
                Platform.runLater(() -> Toolkit.getToolkit().exitNestedEventLoop(key, null));
            });
            Toolkit.getToolkit().enterNestedEventLoop(key);
        });

        webEngine.setConfirmHandler(param -> {
            final Object key = new Object();
            ApplicationManager.getApplication().invokeLater(() -> {
                final boolean result = Messages.OK == Messages.showOkCancelDialog(String.valueOf(param),
                        webEngine.getLocation(), "OK", "CANCEL", null);
                Platform.runLater(() -> Toolkit.getToolkit().exitNestedEventLoop(key, result));
            });
            return (Boolean) Toolkit.getToolkit().enterNestedEventLoop(key);
        });

        webEngine.setPromptHandler(e -> {
            final Object key = new Object();
            ApplicationManager.getApplication().invokeLater(() -> {
                final String result = Messages.showInputDialog(String.valueOf(e), webEngine.getLocation(), null,
                        e.getDefaultValue(), null);
                Platform.runLater(() -> Toolkit.getToolkit().exitNestedEventLoop(key, result));
            });
            return (String) Toolkit.getToolkit().enterNestedEventLoop(key);
        });

        return webView;
    }
}
