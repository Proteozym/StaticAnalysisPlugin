package hudson.plugins.analysis.util.model;

import java.util.Collection;

/**
 * Annotates a collection of line ranges in a file. An annotation consists of a description and a tooltip.
 *
 * @author Ulli Hafner
 */
public interface FileAnnotation extends Comparable<FileAnnotation> {
    /**
     * Returns the message of this annotation.
     *
     * @return the message of this annotation
     */
    String getMessage();

    /**
     * Returns the a detailed description that will be used as tooltip.
     *
     * @return the tooltip of this annotation
     */
    String getToolTip();

    /**
     * Returns the primary line number of this annotation that defines the anchor of this annotation.
     *
     * @return the primary line number of this annotation
     */
    int getPrimaryLineNumber();

    /**
     * Returns a collection of line ranges for this annotation.
     *
     * @return the collection of line ranges for this annotation.
     */
    Collection<LineRange> getLineRanges();

    /**
     * Returns the unique key of this annotation.
     *
     * @return the unique key of this annotation.
     */
    long getKey();

    /**
     * Returns the priority of this annotation.
     *
     * @return the priority of this annotation
     */
    Priority getPriority();

    /**
     * Returns the absolute path of the workspace file that contains this annotation.
     *
     * @return the name of the workspace file that contains this annotation
     */
    String getFileName();

    /**
     * Returns the name of this annotation that could be used as text in links.
     *
     * @return the link name of this duplication
     */
    String getLinkName();

    /**
     * Sets the file name to the specified value.
     *
     * @param fileName the value to set
     */
    void setFileName(final String fileName);

    /**
     * Sets the pathname for this warning.
     *
     * @param workspacePath the workspace path
     */
    void setPathName(final String workspacePath);

    /**
     * Gets the associated file name of this bug (without path).
     *
     * @return the short file name
     */
    String getShortFileName();

    /**
     * Returns the name of the maven or ant module that contains this annotation.
     *
     * @return the name of the module that contains this annotation
     */
    String getModuleName();

    /**
     * Sets the name of the maven or ant module that contains this annotation.
     *
     * @param moduleName the name of the module that contains this annotation
     */
    void setModuleName(String moduleName);

    /**
     * Returns the name of package (or namespace) that contains this annotation.
     *
     * @return the name of the package that contains this annotation
     */
    String getPackageName();

    /**
     * Returns whether a package name is defined for this annotation.
     *
     * @return <code>true</code> if this annotation has a package or namespace name, <code>false</code> otherwise
     */
    boolean hasPackageName();

    /**
     * Returns the path name of this annotation (relative path to the affected file).
     *
     * @return the path name
     */
    String getPathName();

    /**
     * Returns the origin of the annotation. Might be an empty string.
     *
     * @return the origin of the annotation
     */
    String getOrigin();

    /**
     * Returns the category of the annotation. Might be an empty string if there is no category.
     *
     * @return the annotation category
     */
    String getCategory();

    /**
     * Returns the annotation type. Might be an empty string if there is no type.
     *
     * @return the annotation type
     */
    String getType();

    /**
     * Returns a hash code of the surrounding context of this annotation.
     *
     * @return a hash code of the surrounding context of this annotation.
     */
    long getContextHashCode();

    /**
     * Sets the context hash code to the specified value.
     *
     * @param contextHashCode the value to set
     */
    void setContextHashCode(long contextHashCode);

    /**
     * Returns the start column of the position of this warning.
     *
     * @return the start column
     * @since 1.52
     */
    int getColumnStart();

    /**
     * Returns the end column of the position of this warning.
     *
     * @return the end column
     * @since 1.52
     */
    int getColumnEnd();

    /**
     * Returns whether this annotation is in the console log or in a file in the filesystem.
     *
     * @return <code>true</code> if this annotation is in the console log, or <code>false</code> if this annotation is
     * in a file in the filesystem.
     * @since 1.53
     */
    boolean isInConsoleLog();
}
