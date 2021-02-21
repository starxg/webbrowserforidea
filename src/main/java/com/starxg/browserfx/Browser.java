package com.starxg.browserfx;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import org.apache.commons.lang3.StringUtils;

import com.shopobot.util.URL;

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
        if (webView.type() == BrowserView.Type.JAVAFX) {
            panel.add(new ControlButton("DevTools") {
                {
                    addActionListener(e -> webView.openDevTools());
                    setToolTipText("DevTools");
                }
            });
        }
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

        webView.onUrlChange(s -> swingInvokeLater(() -> {
            if (StringUtils.isBlank(s) || "about:blank".equals(StringUtils.trim(s))) {
                txtUrl.setText(StringUtils.EMPTY);
                return;
            }

            txtUrl.setText(s);

            // 没有获取焦点的时候光标回到0
            if (!txtUrl.isFocusOwner()) {
                txtUrl.setCaretPosition(0);
            }
        }));

        webView.onProgressChange(e -> swingInvokeLater(() -> {
            progressBar.setVisible(e != 1.0 && e != 0);
            progressBar.setValue((int) (e * 100));
        }));

        // txtUrl.setText("http://127.0.0.1:8080");
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
        try {
            webView.load(URL.get(url).toJavaURL().toString());
        } catch (Exception e) {
            webView.load("about:blank");
        }
    }

    private static class ControlButton extends JButton {
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
