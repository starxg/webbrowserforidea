package com.starxg.browserfx;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * 工厂
 * 
 * @author huangxingguang
 * @date 2019-04-21 14:55
 */
public class BrowserWindowFactory implements ToolWindowFactory {

    public BrowserWindowFactory() {

    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(new Browser(new JavaFxBrowser()), "", false);
        toolWindow.getContentManager().addContent(content);
    }

}