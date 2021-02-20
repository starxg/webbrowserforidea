package com.starxg.browserfx;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.CountDownLatch;

import javax.swing.*;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.shopobot.util.URL;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;

/**
 * 主面板
 * 
 * @author huangxingguang
 * @date 2019-04-21 13:53
 */
class Browser extends JPanel {
    private BrowserView webView;
    private JButton btnBack;
    private JButton btnForward;
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
        panel.add(btnBack = new ControlButton("<"));
        panel.add(btnForward = new ControlButton(">"));
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
        progressBar.setVisible(true);
        progressBar.setPreferredSize(new Dimension(0, 2));
        panel.add(progressBar, BorderLayout.NORTH);
        panel.add(webView.getBrowser(), BorderLayout.CENTER);
        return panel;
    }

    private void initEvent() {
        btnGo.addActionListener(e -> load(txtUrl.getText()));

        webView.onUrlChange(s -> {
            this.txtUrl.setText(s);
            this.btnBack.setEnabled(webView.canForward());
            this.btnForward.setEnabled(webView.canBack());
        });

        webView.onLoadError(e -> progressBar.setVisible(false));

        webView.onProgressChange(e -> {
            progressBar.setVisible(e != 1.0);
            progressBar.setValue((int) (e * 100));
        });

        webView.onAlert(
                e -> ApplicationManager.getApplication().invokeLater(() -> Messages.showDialog(String.valueOf(e),
                        txtUrl.getText(), new String[] { "OK" }, 0, Messages.getInformationIcon())));

        webView.onConfirm(e -> {
            ReadOnlyBooleanWrapper wrapper = new ReadOnlyBooleanWrapper(false);
            try {
                CountDownLatch latch = new CountDownLatch(1);
                ApplicationManager.getApplication().invokeLater(() -> {
                    wrapper.set(Messages.OK == Messages.showOkCancelDialog(String.valueOf(e), txtUrl.getText(),
                            Messages.getInformationIcon()));
                    latch.countDown();
                });
                latch.await();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return wrapper.get();
        });

        webView.onPrompt((msg, defaultValue) -> {
            ReadOnlyStringWrapper wrapper = new ReadOnlyStringWrapper(null);
            try {
                CountDownLatch latch = new CountDownLatch(1);
                ApplicationManager.getApplication().invokeLater(() -> {
                    wrapper.set(
                            Messages.showInputDialog(String.valueOf(msg), txtUrl.getText(), null, defaultValue, null));
                    latch.countDown();
                });
                latch.await();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return wrapper.get();
        });

        btnBack.addActionListener(e -> webView.back());
        btnForward.addActionListener(e -> webView.forward());
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
        webView.load(URL.get(url).toJavaURL().toString());
    }

    private static final class ControlButton extends JButton {
        ControlButton(String text) {
            super(text);
            setMaximumSize(new Dimension(40, 25));
            setMinimumSize(new Dimension(40, 25));
            setPreferredSize(new Dimension(40, 25));
        }
    }
}
