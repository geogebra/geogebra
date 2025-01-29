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
