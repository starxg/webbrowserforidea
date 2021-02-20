package com.starxg.browserfx;

import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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

    Browser(BrowserView webView) {
        this.webView = webView;
        this.initView();
        this.initEvent();
    }

    private void initView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 20, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);

        btnBack = new JButton("<");
        btnBack.setEnabled(false);
        btnBack.setMaximumSize(new Dimension(40, 29));
        btnBack.setMinimumSize(new Dimension(40, 29));
        btnBack.setPreferredSize(new Dimension(40, 29));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(0, 0, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(btnBack, gbc);

        btnForward = new JButton(">");
        btnForward.setEnabled(false);
        btnForward.setMaximumSize(new Dimension(40, 29));
        btnForward.setMinimumSize(new Dimension(40, 29));
        btnForward.setPreferredSize(new Dimension(40, 29));
        btnForward.setMargin(JBUI.emptyInsets());
        gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(0, 0, 5, 5);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(btnForward, gbc);

        txtUrl = new JTextField();
        gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(0, 0, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 2;
        gbc.gridy = 0;
        add(txtUrl, gbc);
        txtUrl.setColumns(10);

        btnGo = new JButton("Go");
        btnGo.setMaximumSize(new Dimension(50, 29));
        btnGo.setMinimumSize(new Dimension(50, 29));
        btnGo.setPreferredSize(new Dimension(50, 29));
        gbc = new GridBagConstraints();
        gbc.insets = JBUI.insetsBottom(5);
        gbc.gridx = 3;
        gbc.gridy = 0;
        add(btnGo, gbc);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
        panel.add(webView.getBrowser(), BorderLayout.CENTER);
        gbc = new GridBagConstraints();
        gbc.gridwidth = 4;
        gbc.insets = JBUI.insetsRight(5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(panel, gbc);
    }

    private void initEvent() {
        btnGo.addActionListener(e -> webView.load(txtUrl.getText()));
        webView.urlChange(s -> {
            this.txtUrl.setText(s);
            this.btnBack.setEnabled(webView.isPrev());
            this.btnForward.setEnabled(webView.isNext());
        });
        btnBack.addActionListener(e -> webView.back());
        btnForward.addActionListener(e -> webView.forward());
        txtUrl.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && txtUrl.getText().trim().length() > 0) {
                    webView.load(txtUrl.getText());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }
}
