package toolwindow;

import com.intellij.ide.TreeExpander;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import hudson.plugins.analysis.util.model.FileAnnotation;
import utility.ResultsCollector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Extends the JPanel Swing Class. Is used to create the CheckPluginToolWindow content.
 * @author Jonas Safranek
 */
public class CheckPanel extends JPanel {
    private JLabel checkTool;
    private HashMap<String, HashMap<String, HashMap<String, HashMap<String, LinkedList<FileAnnotation>>>>> resultMap;
    private  JTree tree;
    private ResultsCollector resCol;
    private Project projectData;

    /**
     *
     * @param project the IntelliJ project object
     */
    public CheckPanel(final Project project) {

        super(new BorderLayout());

        setBorder(new EmptyBorder(2, 2, 2, 2));

        projectData = project;

        CheckPluginDefaultMutableTreeNode root = new CheckPluginDefaultMutableTreeNode("Results");
        tree = new Tree(root);
        ToolTipManager.sharedInstance().registerComponent(tree);
        /*resultPanel = new JPanel();

        resultPanel.add(tree);
*/
        ActionGroup actionGrp = createActions();
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("CheckPluginToolWindow", actionGrp, false);
        toolbar.getComponent().setVisible(true);
        add(toolbar.getComponent(), BorderLayout.WEST);
        add(new JBScrollPane(tree), BorderLayout.CENTER);
        resCol = new ResultsCollector(project);

        resultMap = resCol.getResultsContent();
        addListeners();
        if (resultMap == null) return;
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(final TreeSelectionEvent treeSelectionEvent) {
                //if (scrolling) {
                CheckPluginDefaultMutableTreeNode treeNode = (CheckPluginDefaultMutableTreeNode) treeSelectionEvent.getPath().getLastPathComponent();
                highlightError(treeNode);
                //}
            }
        });
        //Needs to be in the HashMap

        for (HashMap.Entry<String, HashMap<String, HashMap<String, HashMap<String, LinkedList<FileAnnotation>>>>> results : resultMap.entrySet()) {
            CheckPluginDefaultMutableTreeNode typeContainer = new CheckPluginDefaultMutableTreeNode(results.getKey());

            for (HashMap.Entry<String, HashMap<String, HashMap<String, LinkedList<FileAnnotation>>>> resultsCat : results.getValue().entrySet()) {
                CheckPluginDefaultMutableTreeNode category = new CheckPluginDefaultMutableTreeNode(resultsCat.getKey());
                typeContainer.add(category);
                for (HashMap.Entry<String, HashMap<String, LinkedList<FileAnnotation>>> resultsRule : resultsCat.getValue().entrySet()) {
                    CheckPluginDefaultMutableTreeNode rule = new CheckPluginDefaultMutableTreeNode(resultsRule.getKey());
                    category.add(rule);
                    for (HashMap.Entry<String, LinkedList<FileAnnotation>> resultsFile : resultsRule.getValue().entrySet()) {
                        CheckPluginDefaultMutableTreeNode file = new CheckPluginDefaultMutableTreeNode(resultsFile.getKey());
                        rule.add(file);
                        for (FileAnnotation message : resultsFile.getValue()) {
                            file.add(new CheckPluginDefaultMutableTreeNode(message));
                        }
                    }
                }
            }
            root.add(typeContainer);
        }

    }

    //Add mouse listener to support double click and popup actions.

    /**
     *
     * @return The CheckPluginDefaultMutableTreeNode-object of the warning that the user interacted with
     */
    public CheckPluginDefaultMutableTreeNode[] addListeners() {
        tree.addMouseListener(new MouseAdapter() {
            //Get the current tree node where the mouse event happened
            private CheckPluginDefaultMutableTreeNode[] getNodeFromEvent(final MouseEvent e) {
                TreePath[] selectionPaths = tree.getSelectionPaths();
                if (selectionPaths != null) {
                    CheckPluginDefaultMutableTreeNode[] result = new CheckPluginDefaultMutableTreeNode[selectionPaths.length];
                    for (int i = 0; i < result.length; i++) {
                        result[i] = (CheckPluginDefaultMutableTreeNode) selectionPaths[i].getLastPathComponent();
                    }
                    return result;
                }
                return null;
            }

            public void mousePressed(final MouseEvent e) {
                CheckPluginDefaultMutableTreeNode[] treeNodes = getNodeFromEvent(e);
                if (treeNodes != null) {
                    if (e.getClickCount() == 2) {
                        for (CheckPluginDefaultMutableTreeNode treeNode : treeNodes) {
                            highlightError(treeNode);
                        }
                    }
                }
            }
        });
        return null;
    }
    /**
     * Highlights a given violation/error represented by the given tree node.
     *
     * @param annot Fileannot of the violation
     */
    public void highlightError(final CheckPluginDefaultMutableTreeNode annot) {
        if (annot != null) {
            Object obj = annot.getUserObject();
            if (obj instanceof FileAnnotation) {
                openEditor((FileAnnotation) obj);
            }
        }
    }

    /**
     * Opens the given violation's file in the Editor and returns the Editor.
     *
     * @param result The Violation
     * @return the editor with caret at the violation
     */
    private Editor openEditor(final FileAnnotation result) {

        FileEditorManager fileEditorManager = FileEditorManager.getInstance(projectData);
        final VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(result.getFileName().replace(File.separatorChar, '/'));
        if (virtualFile != null) {
            return fileEditorManager.openTextEditor(new OpenFileDescriptor(
                            projectData,
                            virtualFile,
                            Math.max(result.getPrimaryLineNumber() - 1, 0),
                            Math.max(result.getColumnStart() - 1, 0)),
                    true);
        }
        return null;
    }

    /**
     *
     * @return ActionGroup element to add to the toolbar
     */
    private ActionGroup createActions() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
//        actionGroup.add(new ReRunAction());
//        actionGroup.add(new CloseAction());

        // TreeExpander for expand/collapse all.
        TreeExpander treeExpander = new TreeExpander() {
            public void expandAll() {
                TreeUtil.expandAll(tree);
            }

            public boolean canExpand() {
                return true;
            }

            public void collapseAll() {
                TreeUtil.collapseAll(tree, 1);
            }

            public boolean canCollapse() {
                return true;
            }
        };

//       actionGroup.add(OpenApiAdapter.getInstance().createCollapseAllAction(treeExpander, this));
//       actionGroup.add(OpenApiAdapter.getInstance().createExpandAllAction(treeExpander, this));
        return actionGroup;
    }

}
