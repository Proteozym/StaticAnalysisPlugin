package utility;

import com.intellij.openapi.project.Project;

import hudson.plugins.analysis.core.AbstractAnnotationParser;

import hudson.plugins.analysis.util.model.FileAnnotation;


import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * The class which collects the results of the analysis plugins and sorts them in a HashMap for displaying.
 * @author Jonas Safranek.
 */
public class ResultsCollector {

    private Collection<FileAnnotation> resultsContent = null;
    private Project project;
    private PomUtil objPom = null;

    /**
     *
     * @param proj the IntelliJ Project object
     */
    public ResultsCollector(Project proj) {
        project = proj;
    }

    /**
     *
     * @return the filtered and sorted HashMap for displaying in the tool window
     */
    public HashMap<String, HashMap<String, HashMap<String, HashMap<String, LinkedList<FileAnnotation>>>>> getResultsContent() {

        HashMap<String, Collection<FileAnnotation>> result = readResults();
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, LinkedList<FileAnnotation>>>>> returnMap = orderResults(result);

        return returnMap;

    }

    private HashMap<String, HashMap<String, HashMap<String, HashMap<String, LinkedList<FileAnnotation>>>>> orderResults(final HashMap<String, Collection<FileAnnotation>> resultMap) {

        if (resultMap == null) return null;
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, LinkedList<FileAnnotation>>>>> returnValue = new HashMap<>();
        HashMap<String, HashMap<String, HashMap<String, LinkedList<FileAnnotation>>>> orderedResults = new HashMap<String, HashMap<String, HashMap<String, LinkedList<FileAnnotation>>>>();
        for (HashMap.Entry<String, Collection<FileAnnotation>> result : resultMap.entrySet()) {

            for (FileAnnotation annot : result.getValue()) {
                if (annot == null) return null;

                if (!orderedResults.containsKey(annot.getCategory())) {
                    if (annot.getCategory() == null) return null;
                    orderedResults.put(annot.getCategory(), new HashMap<String, HashMap<String, LinkedList<FileAnnotation>>>());
                }
                if (!orderedResults.get(annot.getCategory()).containsKey(annot.getType())) {
                    if (annot.getPackageName() == null) return null;
                    orderedResults.get(annot.getCategory()).put(annot.getType(), new HashMap<String, LinkedList<FileAnnotation>>());
                }
                if (!orderedResults.get(annot.getCategory()).get(annot.getType()).containsKey(annot.getShortFileName())) {
                    if (annot.getFileName() == null) return null;
                    orderedResults.get(annot.getCategory()).get(annot.getType()).put(annot.getShortFileName(), new LinkedList<FileAnnotation>());
                }
                if (!orderedResults.get(annot.getCategory()).get(annot.getType()).get(annot.getShortFileName()).contains(annot)) {
                    orderedResults.get(annot.getCategory()).get(annot.getType()).get(annot.getShortFileName()).add(annot);
                }
                returnValue.put(result.getKey(), orderedResults);

            }
            orderedResults = new HashMap<String, HashMap<String, HashMap<String, LinkedList<FileAnnotation>>>>();
        }
        return returnValue;
    }


    private HashMap<String, Collection<FileAnnotation>> readResults() {
        ProjectData projectData = ProjectData.getInstance();
        if (projectData == null) return null;
        PomUtil pomUtil = new PomUtil(project, projectData.getEvent().getDataContext());
        ArrayList<String> execList = projectData.getExecParsersList();

        HashMap<String, Collection<FileAnnotation>> parserValues = new HashMap<>();

        if (pomUtil != null) {
            try {
                for (String parserType : execList) {
                    String resultsDir = pomUtil.getTestOutput(projectData.getMavenProject(), parserType);
                    AbstractAnnotationParser parser = ParserUtil.createParser(parserType);
                    if (read(resultsDir, parserType) != null) parserValues.put(parserType, parser.parse(read(resultsDir, parserType), "-"));
                }
                return parserValues;
            } catch (Exception e) {
                throw new RuntimeException("Can't parse the result files!", e);
            }
        }
        return null;
    }

    /**
     * Temporary workaround for the pom.xml convention issues regarding the declaration of an export file or an export directory for maven analysis plugins.
     * @param fileName
     * @param type
     * @return
     */
    private InputStream read(final String fileName, final String type) {
        try {
            Path file = new java.io.File(fileName).toPath();
            FileInputStream readFile;
            if (Files.isDirectory(file)) {

                readFile = new FileInputStream(fileName+ "/" + type + ".xml");
            }
            else {
                readFile = new FileInputStream(fileName);
            }

            return (readFile);
        } catch (Exception e) {
            throw new RuntimeException("Can't find the result file " + fileName, e);
        }


    }


}
