package hudson.plugins.analysis.util;

import javax.annotation.CheckForNull;
import java.io.InputStream;

/**
 * Detects the package or namespace name of a file.
 *
 * @author Ulli Hafner
 */
public interface PackageDetector {
    /**
     * Detects the package or namespace name of the specified input stream. The
     * stream must be closed afterwards.
     *
     * @param stream
     *            the content of the file to scan
     * @param encoding
     *            the encoding of the file, if <code>null</code> or empty then
     *            the default encoding of the platform is used
     * @return the detected package or namespace name
     */
    String detectPackageName(final InputStream stream, @CheckForNull final String encoding);

    /**
     * Detects the package or namespace name of the specified input stream. The
     * stream must be closed afterwards.
     *
     * @param fileName
     *            the file name of the file to scan
     * @param encoding
     *            the encoding of the file, if <code>null</code> or empty then
     *            the default encoding of the platform is used
     * @return the detected package or namespace name
     */
    String detectPackageName(final String fileName, @CheckForNull final String encoding);

    /**
     * Returns whether this classifier accepts the specified file for
     * processing.
     *
     * @param fileName
     *            the file name
     * @return <code>true</code> if the classifier accepts the specified file
     *         for processing.
     */
    boolean accepts(String fileName
   );
}
