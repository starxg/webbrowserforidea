package com.starxg.browserfx;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.collections.ListChangeListener;
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
 * @author huangxingguang
 * @date 2019-04-21 13:52
 */
public class JavaFxBrowser implements BrowserView {
    private WebView browser;
    private WebEngine webEngine;
    private JFXPanel jfxPanel;
    private int index;

    public JavaFxBrowser() {
        jfxPanel = new JFXPanel();
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            browser = new WebView();
            webEngine = browser.getEngine();
            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(browser);
            Scene scene = new Scene(borderPane);
            jfxPanel.setScene(scene);
            webEngine.getHistory().getEntries().addListener((ListChangeListener<WebHistory.Entry>) c -> {
                c.next();
                index = c.getAddedSize();
            });

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
        Platform.runLater(() -> webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> consumer.accept(webEngine.getLocation())));
    }

    @Override
    public void back() {
        Platform.runLater(() -> {
            if (isPrev()) {
                index++;
                webEngine.getHistory().go(-1);
            }
        });

    }

    @Override
    public void forward() {
        Platform.runLater(() -> {
            if (isNext()) {
                index--;
                webEngine.getHistory().go(1);
            }
        });
    }

    @Override
    public boolean isNext() {
        return webEngine.getHistory().getCurrentIndex() < (index - 1);
    }

    @Override
    public boolean isPrev() {
        return webEngine.getHistory().getCurrentIndex() > 0;
    }
}
