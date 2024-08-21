package org.geogebra.common.contextmenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.util.Range;

public class AttributedString {
    public enum Attribute {
        Subscript
    }

    private final String rawValue;
    private final Map<Attribute, Set<Range>> attributes;

    public AttributedString(String rawValue) {
        this.rawValue = rawValue;
        this.attributes = new HashMap<>();
    }

    public void add(Attribute attribute, Range range) {
        attributes.computeIfAbsent(attribute, k -> new HashSet<>());
        attributes.get(attribute).add(range);
    }

    @CheckForNull
    public Set<Range> getAttribute(Attribute attribute) {
        return attributes.get(attribute);
    }

    @Override
    public String toString() {
        return rawValue;
    }

    @Nonnull
    public static AttributedString parseString(String str) {
        StringBuilder parsedString = new StringBuilder(str);
        List<Range> subscriptRanges = new ArrayList<>();

        Range subscriptRange = findRawSubscript(str, 0);
        while (subscriptRange != null) {
            String subscript = parsedString.substring(subscriptRange.getStart() + 2,
                    subscriptRange.getEnd() - 1);
            parsedString.replace(subscriptRange.getStart(), subscriptRange.getEnd(), subscript);
            subscriptRanges.add(Range.of(subscriptRange.getStart(), subscriptRange.getEnd() - 3));
            subscriptRange = findRawSubscript(parsedString.toString(),
                    subscriptRange.getEnd() - 3);
        }

        AttributedString attributedString = new AttributedString(parsedString.toString());
        subscriptRanges.forEach(r -> attributedString.add(Attribute.Subscript, r));

        return attributedString;
    }

    @CheckForNull
    private static Range findRawSubscript(String rawText, int fromIndex) {
        int start = rawText.indexOf("_{", fromIndex);
        if (start != -1) {
            int end = rawText.indexOf("}", fromIndex + 2);
            if (end != -1) {
                return Range.of(start, end + 1);
            }
        }
        return null;
    }
}
