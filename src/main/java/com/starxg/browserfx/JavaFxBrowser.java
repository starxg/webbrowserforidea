package com.starxg.browserfx;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.util.function.Consumer;

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

    JavaFxBrowser() {
        jfxPanel = new JFXPanel();
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            browser = new WebView();
            webEngine = browser.getEngine();
            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(browser);
            Scene scene = new Scene(borderPane);
            jfxPanel.setScene(scene);
        });
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
    public void urlChange(Consumer<String> consumer) {
        Platform.runLater(() -> webEngine.getLoadWorker().stateProperty()
                .addListener((ov, oldState, newState) -> consumer.accept(webEngine.getLocation())));
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
    public boolean isNext() {
        final WebHistory history = webEngine.getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        return entryList.size() > 1 && history.getCurrentIndex() < entryList.size() - 1;
    }

    @Override
    public boolean isPrev() {
        final WebHistory history = webEngine.getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        return entryList.size() > 1 && history.getCurrentIndex() > 0;
    }
}
