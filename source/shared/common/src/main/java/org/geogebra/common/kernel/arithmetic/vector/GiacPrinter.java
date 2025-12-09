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

package org.geogebra.common.kernel.arithmetic.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;

class GiacPrinter {

    public static String print(StringTemplate tpl, String xCoord, String yCoord,
			PrintableVector vector) {
        StringBuilder sb = new StringBuilder();
        sb.append(getHead(vector));
        printReGiac(sb, xCoord, vector.getX());
        sb.append(",");
        printReGiac(sb, yCoord, vector.getY());
        sb.append(getTail(vector));
        return sb.toString();
    }

    private static String getHead(PrintableVector vector) {
        if (vector.isCASVector()) {
            return "ggbvect[";
        } else if (GeoSymbolic.isWrappedList(vector.getX())
                && GeoSymbolic.isWrappedList(vector.getY())) {
            return "zip((x,y)->point(x,y),";
        } else {
            return "point(";
        }
    }

    private static char getTail(PrintableVector vector) {
        return vector.isCASVector() ? ']' : ')';
    }

    private static void printReGiac(
            StringBuilder sb,
            String expressionValue,
            ExpressionValue value) {

        if (value.unwrap() instanceof Command) {
            sb.append("re(");
        }
        sb.append(expressionValue);
        if (value.unwrap() instanceof Command) {
            sb.append(")");
        }

    }
}
