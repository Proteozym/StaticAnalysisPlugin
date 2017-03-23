package hudson.plugins.analysis.util.model;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

/**
 * Defines the priority of an annotation.
 *
 * @author Ulli Hafner
 */
public enum Priority {
    /** High priority. */
    HIGH,
    /** Normal priority. */
    NORMAL,
    /** Low priority. */
    LOW;

    /**
     * Converts a String priority to an actual enumeration value.
     *
     * @param priority
     *            priority as a String
     * @return enumeration value.
     */
    public static Priority fromString(final String priority) {
        return Priority.valueOf(StringUtils.upperCase(priority));
    }

    /**
     * Returns a localized description of this priority.
     *
     * @return localized description of this priority
     */
    public String getLocalizedString() {
        if (this == HIGH) {
            return "High";
        }
        if (this == LOW) {
            return "Low";
        }
        return "Normal";
    }

    /**
     * Returns a long localized description of this priority.
     *
     * @return long localized description of this priority
     */
    public String getLongLocalizedString() {
        if (this == Priority.HIGH) {
            return "High Priority";
        }
        if (this == Priority.LOW) {
            return "Low Priority";
        }
        return "Normal Priority";
    }

    /**
     * Gets the priorities starting from the specified priority to
     * {@link Priority#HIGH}.
     *
     * @param minimumPriority
     *            the minimum priority
     * @return the priorities starting from the specified priority
     */
    public static Collection<Priority> collectPrioritiesFrom(final Priority minimumPriority) {
        ArrayList<Priority> priorities = new ArrayList<Priority>();
        priorities.add(Priority.HIGH);
        if (minimumPriority == Priority.NORMAL) {
            priorities.add(Priority.NORMAL);
        }
        if (minimumPriority == Priority.LOW) {
            priorities.add(Priority.NORMAL);
            priorities.add(Priority.LOW);
        }
        return priorities;
    }
}