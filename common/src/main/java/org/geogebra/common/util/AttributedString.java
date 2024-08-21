package org.geogebra.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * String representation that can have attributes applied to specific ranges of characters.
 */
public class AttributedString {
    /**
     * Possible attributes that can be applied to specific ranges of characters.
     */
    public enum Attribute {
        Subscript
    }

    private final String rawValue;
    private final Map<Attribute, Set<Range>> attributes;

    /**
     * @param rawValue The raw value of the string.
     */
    public AttributedString(String rawValue) {
        this.rawValue = rawValue;
        this.attributes = new HashMap<>();
    }

    /**
     * Adds an attribute to the specified range of characters.
     * @param attribute The attribute to apply.
     * @param range The range of characters to which the attribute should be applied.
     */
    public void add(Attribute attribute, Range range) {
        attributes.computeIfAbsent(attribute, k -> new HashSet<>());
        attributes.get(attribute).add(range);
    }

    /**
     * Retrieves the set of ranges where the specified attribute is applied.
     *
     * @param attribute The attribute to look for.
     * @return A set of {@link Range} objects where the attribute is applied, or {@code null} if
     * the attribute is not present.
     */
    @CheckForNull
    public Set<Range> getAttribute(Attribute attribute) {
        return attributes.get(attribute);
    }

    /**
     * Parses a string that may contain subscript markup into an {@code AttributedString}.
     * The method identifies subscript sequences (formatted as "_{...}") and converts them into
     * subscript attributes within the returned {@code AttributedString}.
     *
     * @param str The input string potentially containing subscript markup.
     * @return An {@code AttributedString} with subscript attributes applied as needed.
     */
    @Nonnull
    public static AttributedString parseString(String str) {
        StringBuilder parsedString = new StringBuilder(str);
        List<Range> subscriptRanges = new ArrayList<>();

        Range subscriptRange = findRawSubscript(str, 0);
        while (subscriptRange != null) {
            String subscript = parsedString.substring(subscriptRange.getStart() + 2,
                    subscriptRange.getEnd() - 1);
            parsedString.replace(subscriptRange.getStart(), subscriptRange.getEnd(), subscript);
            subscriptRanges.add(new Range(subscriptRange.getStart(), subscriptRange.getEnd() - 3));
            subscriptRange = findRawSubscript(parsedString.toString(),
                    subscriptRange.getEnd() - 3);
        }

        AttributedString attributedString = new AttributedString(parsedString.toString());
        subscriptRanges.forEach(r -> attributedString.add(Attribute.Subscript, r));

        return attributedString;
    }

    @Override
    public String toString() {
        return rawValue;
    }

    @CheckForNull
    private static Range findRawSubscript(String rawText, int fromIndex) {
        int start = rawText.indexOf("_{", fromIndex);
        if (start != -1) {
            int end = rawText.indexOf("}", fromIndex + 2);
            if (end != -1) {
                return new Range(start, end + 1);
            }
        }
        return null;
    }
}
