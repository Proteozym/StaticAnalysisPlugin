package hudson.plugins.analysis.util;

import javax.annotation.CheckForNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Validates a file encoding. The encoding must be an encoding ID supported by
 * the underlying Java platform.
 *
 * @author Ulli Hafner
 */
public final class EncodingValidator {
    /** All available character sets. */
    private static Set<String> allCharacterSets;

    static {
        try {
            allCharacterSets = Collections.unmodifiableSet(new HashSet<String>(Charset
                    .availableCharsets().keySet()));
        }
        // CHECKSTYLE:OFF
        catch (Exception exception) { // NOPMD
            allCharacterSets = Collections.emptySet();
        }
        // CHECKSTYLE:ON
    }

    /**
     * Returns all available character set names.
     *
     * @return all available character set names
     */
    public static Set<String> getAvailableCharsets() {
        return allCharacterSets;
    }

    /**
     * Returns the default charset for the specified encoding string. If the
     * default encoding is empty or <code>null</code>, or if the charset is not
     * valid then the default encoding of the platform is returned.
     *
     * @param defaultEncoding
     *            identifier of the character set
     * @return the default charset for the specified encoding string
     */
    public static Charset defaultCharset(@CheckForNull final String defaultEncoding) {
        try {
            if (StringUtils.isNotBlank(defaultEncoding)) {
                return Charset.forName(defaultEncoding);
            }
        }
        catch (UnsupportedCharsetException exception) {
            // ignore and return default
        }
        catch (IllegalCharsetNameException exception) {
            // ignore and return default
        }
        return Charset.defaultCharset();
    }

    /**
     * Reads the specified file with the given encoding.
     *
     * @param fileName
     *            the file name
     * @param encoding
     *            the encoding of the file, if <code>null</code> or empty then
     *            the default encoding of the platform is used
     * @return the line iterator
     * @throws IOException
     *             Signals that an I/O exception has occurred during reading of
     *             the file.
     */
    public static LineIterator readFile(final String fileName, @CheckForNull final String encoding)
            throws IOException {
        return readStream(new FileInputStream(new File(fileName)), encoding);
    }

    /**
     * Reads the specified file with the given encoding.
     *
     * @param stream
     *            the input stream
     * @param encoding
     *            the encoding of the file, if <code>null</code> or empty then
     *            the default encoding of the platform is used
     * @return the line iterator
     * @throws IOException
     *             Signals that an I/O exception has occurred during reading of
     *             the file.
     */
    @SuppressFBWarnings("DM")
    public static LineIterator readStream(final InputStream stream, @CheckForNull final String encoding) throws IOException {
        if (StringUtils.isNotBlank(encoding)) {
            return IOUtils.lineIterator(stream, encoding);
        }
        else {
            return IOUtils.lineIterator(new InputStreamReader(stream));
        }
    }

    /**
     * Returns the encoding used to read a file. If the specified
     * encoding is empty or <code>null</code>, or if the encoding is not valid
     * then the default encoding of the platform is returned.
     *
     * @param encoding
     *            identifier of the character set
     * @return the default encoding for the specified encoding string
     */
    public static String getEncoding(@CheckForNull final String encoding) {
        if (StringUtils.isNotBlank(encoding) && Charset.forName(encoding).canEncode()) {
            return encoding;
        }
        return Charset.defaultCharset().name();
    }

    private EncodingValidator() {
        // prevents instantiation
    }
}
