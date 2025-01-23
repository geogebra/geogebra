package org.geogebra.common.kernel.printing.printer;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.expression.ExpressionPrinter;

public interface Printer {

    String print(StringTemplate tpl, ExpressionPrinter expressionPrinter, PrintableVector vector);
}
