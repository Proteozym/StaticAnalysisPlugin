
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import toolwindow.CheckPanel;
import utility.ProjectData;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowType;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import static sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl.ThreadStateMap.Byte0.runnable;


/**
 * The action of the Check Plugin.
 * @author Jonas Safranek
 */
public class CheckAction extends AnAction {


    @Override
    public void actionPerformed(final AnActionEvent event) {

        Project invokeProject = event.getProject();

        Project project = MavenActionUtil.getProject(event.getDataContext());

        //TODO: implementing a wait-mechanic for the mavenrunnner.run funtionality

        InvokeMavenRunner mvnRunner = new InvokeMavenRunner(invokeProject, event.getDataContext());

        ProjectData instance = ProjectData.getInstance();
        Object lock = instance.getLock();
        mvnRunner.run();

        synchronized (lock) {
            //if (!mvnRunner.isFin) {
                try {
                    lock.wait();
                } catch (Exception e) {
                    throw new RuntimeException("Lock!");
                }
           // }
        }

        if (invokeProject != null) {
            ProjectData.getInstance().setProjectData(event);
        }

        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);

        ToolWindow resultWindow = toolWindowManager.getToolWindow("CheckPluginToolWindow");

        if (resultWindow == null) {
            resultWindow = toolWindowManager.registerToolWindow("CheckPluginToolWindow", true, ToolWindowAnchor.BOTTOM);
            resultWindow.activate(null);
            resultWindow.setAvailable(true, null);
        }
        else {
            resultWindow.getContentManager().removeAllContents(true);
        }
        if (!resultWindow.isActive()) {
            resultWindow.activate(null);
            resultWindow.setAvailable(true, null);
        }
        CheckPanel resultPanel = new CheckPanel(project);
        Content content = ContentFactory.SERVICE.getInstance().createContent(resultPanel, "", false);
        resultWindow.getContentManager().addContent(content);
        resultWindow.setType(ToolWindowType.DOCKED, null);
    }

}
