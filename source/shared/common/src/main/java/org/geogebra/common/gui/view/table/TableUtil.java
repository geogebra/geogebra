/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
        String[] parts = splitByIndices(content == null ? "-" : content);
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
