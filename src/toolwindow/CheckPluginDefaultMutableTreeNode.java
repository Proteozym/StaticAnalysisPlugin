package toolwindow;

import org.apache.commons.lang.StringEscapeUtils;
import hudson.plugins.analysis.util.model.FileAnnotation;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Class to extend the DefaultMutableTreeNode. Overrides the toString-method for custom labeling.
 * @author Jonas Safranek
 */


public class CheckPluginDefaultMutableTreeNode extends DefaultMutableTreeNode {

    private String label = "";

    /**
     * Sets the label for the toString-method to a properly formatted warning message
     * @param object FileAnnotation object of the warning
     */
    public CheckPluginDefaultMutableTreeNode(final FileAnnotation object) {
        super(object);
        label = StringEscapeUtils.unescapeXml(object.getMessage());
    }

    /**
     *
     * @param object variable object
     */
    public CheckPluginDefaultMutableTreeNode(final Object object) {
        super(object);
        label = object.getClass().toString();
    }

    /**
     *
     * @param string regular String object
     */
    public CheckPluginDefaultMutableTreeNode(final String string) {
        super(string);
        label = string;
    }

    /**
     *
     * @return the correct label of the TreeNode based on its object-type
     */
    @Override
    public String toString() {

        return label;
    }

}
