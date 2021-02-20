package com.starxg.browserfx;

import static javax.swing.SpringLayout.*;

import javax.swing.*;

import com.intellij.util.ui.JBUI;
import org.junit.Test;

import java.awt.*;
import java.util.Random;

public class JFrameDemoTest {

    private JButton btnBack;
    private JButton btnForward;
    private JTextField txtUrl;
    private JButton btnGo;

    @Test
    public void test() throws InterruptedException {
        JFrame jf = new JFrame("演示弹性布局");
        jf.setSize(500, 300);
        JPanel jp = new JPanel();
        jf.setContentPane(jp);
        jp.setLayout(new BorderLayout());

        jp.add(topControls(), BorderLayout.NORTH);
        jp.add(centerContent(), BorderLayout.CENTER);

        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setVisible(true); // 显示窗口

        Thread.currentThread().join();
    }

    private JPanel topControls() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.add(btnBack = new JButton("<"));
        panel.add(btnForward = new JButton(">"));
        panel.add(txtUrl = new JTextField(), new GridBagConstraints() {
            {
                weightx = 1;
                fill = GridBagConstraints.HORIZONTAL;
            }
        });
        panel.add(btnGo = new JButton("Go"));
        return panel;
    }

    private JPanel centerContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(new Random().nextInt(100));
        progressBar.setPreferredSize(new Dimension(0, 3));
        panel.add(progressBar, BorderLayout.NORTH);
        panel.add(new JPanel(), BorderLayout.CENTER);
        return panel;
    }
}
