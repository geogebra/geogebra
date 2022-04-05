package org.geogebra.common.gui.view.table;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Table values related utility method collection.
 */
public final class TableUtil {

    /**
     * Returns html string of indexed label
     *
     * @param columnIndex index of column
     * @return html string of indexed label
     */
    public static String getHeaderHtml(TableValuesModel model, int columnIndex) {
        String content = model.getHeaderAt(columnIndex);
        String[] parts = splitByIndices(content);
        return IntStream.range(0, parts.length)
                .mapToObj(i -> i % 2 != 0 ? "<sub>" + parts[i] + "</sub>" : parts[i])
                .collect(Collectors.joining());
    }

    /**
     * Splits content by indices. Odd indexed items are the subscripts.
     *
     * @return array containing label and indices
     */
    public static String[] splitByIndices(String content) {
        String[] labelParts = content.split("_");
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(labelParts[0]);
        for (int i = 1; i < labelParts.length; i++) {
            String part = labelParts[i];
            int index = part.indexOf('}');
            if (part.startsWith("{") && index > -1) {
                retVal.add(part.substring(1, index));
                retVal.add(part.substring(index + 1));
            } else {
                retVal.add(part.substring(0, 1));
                retVal.add(part.substring(1));
            }
        }
        return retVal.toArray(new String[]{});
    }
}
