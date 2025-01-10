package org.geogebra.common.kernel.arithmetic.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.expression.ExpressionPrinter;

class GiacPrinter {

    public static String print(StringTemplate tpl, ExpressionPrinter expressionPrinter,
			PrintableVector vector) {
        StringBuilder sb = new StringBuilder();
        sb.append(getHead(vector));
        printReGiac(sb, vector.getX(), expressionPrinter, tpl);
        sb.append(",");
        printReGiac(sb, vector.getY(), expressionPrinter, tpl);
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
            ExpressionValue expressionValue,
            ExpressionPrinter printer,
            StringTemplate tpl) {

        if (expressionValue.unwrap() instanceof Command) {
            sb.append("re(");
        }
        sb.append(printer.print(expressionValue, tpl));
        if (expressionValue.unwrap() instanceof Command) {
            sb.append(")");
        }

    }
}
