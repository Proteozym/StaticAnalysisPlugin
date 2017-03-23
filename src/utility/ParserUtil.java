package utility;

import hudson.plugins.analysis.core.AbstractAnnotationParser;
import hudson.plugins.checkstyle.parser.CheckStyleParser;
import hudson.plugins.pmd.parser.PmdParser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * All analysis module parser have to be registered in this class in order for them to be used.
 * @author Jonas Safranek
 */
public final class ParserUtil {
    private static final ArrayList<String> ParserList  = new ArrayList<String>();
    private static final HashMap<String, String> OutputList = new HashMap<String, String>();
    private static final HashMap<String, String> GoalList = new HashMap<String, String>();

    static {

        ParserList.add("checkstyle");
        ParserList.add("pmd");


        OutputList.put("checkstyle", "outputFile");
        OutputList.put("pmd", "targetDirectory");


        GoalList.put("checkstyle", "checkstyle:checkstyle");
        GoalList.put("pmd", "pmd:pmd");
    }

    private ParserUtil(){ }

    /**
     *
     * @return A variant of the AbstractAnnotationParser determined by the given type
     * @param type The type of Parser that should be created
     *
     */
    public static AbstractAnnotationParser createParser(final String type) {
       switch (type)
        {
            case "checkstyle":
                return new CheckStyleParser();
            case "pmd":
                return new PmdParser();
        }
        return null;
    }

    /**
     *
     * @return Returns an arraylist with all registered parsers
     */
    public static ArrayList<String> getRegisteredParsers() {

        return ParserList;
    }
    /**
     * @param type type of the analysis plugin
     * @return Returns the Name of the XML-Node of the export directory for an analysis plugin
     */
    public static String getOutputNode(final String type) {

        return OutputList.get(type);
    }
    /**
     * @param type type of the analysis plugin
     * @return Returns the Goal of a given analysis tool
     */
    public static String getGoal(final String type) {

        return GoalList.get(type);
    }
}
