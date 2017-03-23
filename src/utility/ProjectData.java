package utility;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import java.util.ArrayList;
import java.util.concurrent.RunnableFuture;


/**
 * A singleton class to set and get data, that is needed multiple times in different places of the plugin project.
 * @author Jonas Safranek
 */
public class ProjectData {

    private AnActionEvent event = null;
    private Project project;
    private static ProjectData instance = null;
    private MavenProject mavenProject = null;
    private ArrayList<String> execList = new ArrayList<String>();
    private static Object lock = null;

    protected ProjectData() {}

    /**
     *
     * @return The singleton object
     */
    public static ProjectData getInstance() {
        if (instance == null) {

            instance = new ProjectData();
            lock = new Object();
        }
        return instance;
    }

    /**
     * sets the needed project information
     * @param eventP the event data from which the project can be derived
     */
    public void setProjectData(final AnActionEvent eventP) {
        event = eventP;
        project = MavenActionUtil.getProject(event.getDataContext());
    }


    public Object getLock() {
        return this.lock;
    }

    /**
     *
     * @param list the list of filtered parsers that are to be run via maven goals
     */
    public void setExecParsersList(final ArrayList<String> list) {execList = list;}

    /**
     *
     * @return the getter for the aforementioned parser list
     */
    public ArrayList<String> getExecParsersList() {return execList;}

    /**
     *
     * @param proj the MavenProject
     */
    public void setMavenProject(MavenProject proj) {
        mavenProject = proj;
    }

    /**
     *
     * @return the AnActionEvent
     */
    public AnActionEvent getEvent() {return event;}

    /**
     *
     * @return the MavenProject
     */
    public MavenProject getMavenProject() {return mavenProject;}


}
