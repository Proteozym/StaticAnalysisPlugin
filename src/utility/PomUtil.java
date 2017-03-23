package utility;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.idea.maven.project.MavenProject;

import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Provides utility methods for handling pom-settings.
 * @author Jonas Safranek
 */

public class PomUtil {

    private Project project;
    private DataContext eventData = null;
    private String projectContext = null;

    /**
     *
     * @param varProject the IntelliJ project object
     * @param e the DataContext created from an AnActionEvent-object
     */
    public PomUtil(final Project varProject, final DataContext e) {
        project = varProject;
        eventData = e;
        projectContext = varProject.getBasePath();
    }

    /**
     * @return returns the maven project for the current projectcontext
     */
    public MavenProject getMavenProject() {
        if (projectContext != null) {
            VirtualFile file = LocalFileSystem.getInstance().findFileByPath(projectContext);
            if (file.isDirectory()) {
                file = file.findChild("pom.xml");
            }
            if (eventData != null && file != null) {
                MavenProjectsManager manager = MavenProjectsManager.getInstance(MavenActionUtil.getProject(eventData));

                if (manager != null) {
                    MavenProject mavenProject = manager.findProject(file);
                    return mavenProject;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param mavenProject the current maven project
     * @param parserType the parser typ for example checkstyle or pmd
     * @return
     */
    public String getTestOutput(final MavenProject mavenProject, final String parserType) {
        if (mavenProject != null) {
            try {
                String nodeName = ParserUtil.getOutputNode(parserType);
                String result = "";

                List<org.jdom.Element> reportPlugins = mavenProject.getPluginGoalConfiguration("org.apache.maven.plugins", "maven-site-plugin", "site").getChild("reportPlugins").getChildren();
                for (org.jdom.Element plugins : reportPlugins) {
                    if (plugins.getChild("artifactId").getValue().equals("maven-" + parserType + "-plugin")) {
                        result = plugins.getChild("configuration").getChild(nodeName).getValue();
                    }
                }
                return result;
            } catch (Exception e) {
                throw new RuntimeException("Checkstyle config not found in effective POM.xml");
            }
        }
        return null;
    }

    /**
     * @param mavenProject the current maven project
    * @return HashMap of the registered parser list filtered by the installed analysis plugins in the maven pom.
    */
    public ArrayList<String> getFilteredParserList(final MavenProject mavenProject) {
        if (mavenProject != null) {
            ArrayList<String> parsers = ParserUtil.getRegisteredParsers();
            ArrayList<String> comparedList = new ArrayList<String>();

            List<org.jdom.Element> reportPlugins = mavenProject.getPluginGoalConfiguration("org.apache.maven.plugins", "maven-site-plugin", "site").getChild("reportPlugins").getChildren();
            for (String entry : parsers) {
                for (org.jdom.Element plugins : reportPlugins) {
                    if (plugins.getChild("artifactId").getValue().equals("maven-" + entry + "-plugin")) comparedList.add(entry);
                }
            }
            return comparedList;
        }
        return null;
    }
}
