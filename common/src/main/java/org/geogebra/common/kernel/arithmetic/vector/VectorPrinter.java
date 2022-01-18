package org.geogebra.common.kernel.arithmetic.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.expression.ExpressionPrinter;

class VectorPrinter implements Printer {

    private PrintableVector vector;
    private Printer defaultPrinter;
    private Printer editPrinter;
    private Printer latexPrinter;

    VectorPrinter(PrintableVector vector) {
        this.vector = vector;
        defaultPrinter = new DefaultVectorPrinter(vector);
        editPrinter = new EditVectorPrinter(vector);
        latexPrinter = new LatexVectorPrinter(vector);
    }

    @Override
    public String print(StringTemplate tpl, ExpressionPrinter expressionPrinter) {
        return getPrinterFor(tpl).print(tpl, expressionPrinter);
    }

    private Printer getPrinterFor(StringTemplate tpl) {
        // if the vector is actually a pair of lists
        // then on the definition panel it should be printed simply as (x, y)
        if (GeoSymbolic.hasListTwin(vector.getX()) && GeoSymbolic.hasListTwin(vector.getY())) {
            return defaultPrinter;
        } else if (tpl == StringTemplate.editorTemplate) {
            return editPrinter;
        } else if (tpl.isLatex()) {
            return latexPrinter;
        } else {
            return defaultPrinter;
        }
    }
}
