package org.geogebra.regexp.shared.contextmenu;

import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math3.util.Pair;

public class AttributedStringFactory {

    /**
     * Replaces special character sequence with attributes
     *
     * @param text String containing possible <code>_{subscript}</code> special character sequences
     * @return AttributedString containing {@link TextAttribute#SUPERSCRIPT} attribute with
     *         {@link TextAttribute#SUPERSCRIPT_SUB} value
     */
    static AttributedString convertSpecialCharacters(String text) {
        StringBuilder stringBuilder = new StringBuilder(text);
        List<Pair<Integer, Integer>> subscriptRanges = new ArrayList<>();

        // Special character sequence representing a subscript
        Matcher subscriptMatcher = Pattern.compile("_\\{(.+)\\}").matcher(text);

        int offset = 0;
        while (subscriptMatcher.find()) {
            // Start and end index of special subsequence relative to the final string
            int start = subscriptMatcher.start() - offset;
            int end = subscriptMatcher.end() - offset;

            // Value contained in the special subsequence
            String subscript = subscriptMatcher.group(1);

            // Replace special character sequence with the value it contains
            stringBuilder.replace(start, end, subscript);

            // Save the range of the special subscript value
            subscriptRanges.add(Pair.create(start, start + subscript.length()));

            // Update the offset as the string length changes
            offset += end - start - subscript.length() + 1;
        }

        AttributedString attributedString = new AttributedString(stringBuilder.toString());
        for (Pair<Integer, Integer> subscriptRange : subscriptRanges) {
            attributedString.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB,
                    subscriptRange.getFirst(), subscriptRange.getSecond());
        }

        return attributedString;
    }
}
