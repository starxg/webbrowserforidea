package com.starxg.browserfx;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.shopobot.util.URL;
import com.sun.javafx.tk.Toolkit;

import javafx.application.Platform;

/**
 * 主面板
 * 
 * @author huangxingguang
 * @date 2019-04-21 13:53
 */
class Browser extends JPanel {
    private BrowserView webView;
    private JTextField txtUrl;
    private JButton btnGo;
    private JProgressBar progressBar;

    Browser(BrowserView webView) {
        this.webView = webView;
        this.initView();
        this.initEvent();
    }

    private void initView() {
        setLayout(new BorderLayout());
        add(topControls(), BorderLayout.NORTH);
        add(centerContent(), BorderLayout.CENTER);
    }

    private JPanel topControls() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.add(txtUrl = new JTextField(), new GridBagConstraints() {
            {
                weightx = 1;
                fill = GridBagConstraints.HORIZONTAL;
            }
        });
        panel.add(btnGo = new ControlButton("Go"));

        return panel;
    }

    private JPanel centerContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        progressBar = new JProgressBar(0, 100);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(0, 5));
        panel.add(progressBar, BorderLayout.NORTH);
        panel.add(webView.getBrowser(), BorderLayout.CENTER);
        return panel;
    }

    private void initEvent() {
        btnGo.addActionListener(e -> load(txtUrl.getText()));

        webView.onUrlChange(s -> swingInvokeLater(() -> this.txtUrl.setText(s)));

        webView.onProgressChange(e -> swingInvokeLater(() -> {
            progressBar.setVisible(e != 1.0 && e != 0);
            progressBar.setValue((int) (e * 100));
        }));

        webView.onAlert(e -> {
            final Object key = new Object();
            ApplicationManager.getApplication().invokeLater(() -> {
                Messages.showMessageDialog(String.valueOf(e), txtUrl.getText(), null);
                Platform.runLater(() -> Toolkit.getToolkit().exitNestedEventLoop(key, null));
            });
            Toolkit.getToolkit().enterNestedEventLoop(key);
        });

        webView.onConfirm(e -> {
            final Object key = new Object();
            ApplicationManager.getApplication().invokeLater(() -> {
                final boolean result = Messages.OK == Messages.showOkCancelDialog(String.valueOf(e), txtUrl.getText(),
                        null);
                Platform.runLater(() -> Toolkit.getToolkit().exitNestedEventLoop(key, result));
            });
            return (Boolean) Toolkit.getToolkit().enterNestedEventLoop(key);
        });

        webView.onPrompt((msg, defaultValue) -> {
            final Object key = new Object();
            ApplicationManager.getApplication().invokeLater(() -> {
                final String result = Messages.showInputDialog(String.valueOf(msg), txtUrl.getText(), null,
                        defaultValue, null);
                Platform.runLater(() -> Toolkit.getToolkit().exitNestedEventLoop(key, result));
            });
            return (String) Toolkit.getToolkit().enterNestedEventLoop(key);
        });

        txtUrl.setText("http://127.0.0.1:8080");
        txtUrl.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    load(txtUrl.getText());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private void load(String url) {
        if (txtUrl.getText().trim().length() < 1) {
            return;
        }
        swingInvokeLater(() -> webView.load(URL.get(url).toJavaURL().toString()));
    }

    private static final class ControlButton extends JButton {
        ControlButton(String text) {
            super(text);
            setMaximumSize(new Dimension(40, 25));
            setMinimumSize(new Dimension(40, 25));
            setPreferredSize(new Dimension(40, 25));
        }
    }

    private void swingInvokeLater(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }
}
