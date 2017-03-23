import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.execution.MavenExecutionOptions;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import utility.NotifyRunnable;
import utility.ParserUtil;
import utility.PomUtil;
import utility.ProjectData;

import javax.xml.crypto.Data;
import java.util.ArrayList;

/**
 * The class to run the maven goals for the analysis plugins.
 * @author Jonas Safranek
 */
public final class InvokeMavenRunner implements Runnable {

    Project project= null;
    DataContext dataContext = null;
    public boolean isFin = false;
    /**
     *
     * @param project The IntelliJ Project
     * @param dataContext the data context derived from the AnActionEvent-object
     */
    public InvokeMavenRunner(final Project project, final DataContext dataContext){
        this.project = project;
        this.dataContext = dataContext;
    }


    public void run() {
        try {

            org.jetbrains.idea.maven.execution.MavenRunner runner = org.jetbrains.idea.maven.execution.MavenRunner.getInstance(project);
            PomUtil pomUtil = new PomUtil(project, dataContext);
            MavenProject mavenProject = pomUtil.getMavenProject();

            MavenRunnerSettings settings = runner.getState().clone();

            settings.getMavenProperties().put("interactiveMode", "false");
            settings.setRunMavenInBackground(true);

            MavenRunnerParameters params = new MavenRunnerParameters();
            params.setWorkingDirPath(project.getBasePath());

            ArrayList<String> execList = pomUtil.getFilteredParserList(mavenProject);

            ProjectData instance = ProjectData.getInstance();
            instance.setExecParsersList(execList);
            instance.setMavenProject(mavenProject);
            Object lock = instance.getLock();

            MavenProjectsManager manager = new MavenProjectsManager(project);
            manager.getGeneralSettings().setOutputLevel(MavenExecutionOptions.LoggingLevel.DISABLED);

            ArrayList<String> goalList = new ArrayList<>();
            for (String pluginType : execList) {
                goalList.add(ParserUtil.getGoal(pluginType));
            }
            params.setGoals(goalList);
            runner.run(params, settings, new NotifyRunnable());

        } catch (Exception exc) {
            throw new RuntimeException("Project not found!");
        }

    }


}
