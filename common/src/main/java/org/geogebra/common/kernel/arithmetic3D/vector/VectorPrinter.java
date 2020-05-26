package org.geogebra.common.kernel.arithmetic3D.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.expression.ExpressionPrinter;

class VectorPrinter implements Printer {

    private Printer defaultPrinter;
    private Printer editPrinter;
    private Printer latexPrinter;

    VectorPrinter(PrintableVector vector) {
        defaultPrinter = new DefaultVectorPrinter(vector);
        editPrinter = new EditVectorPrinter(vector);
        latexPrinter = new LatexVectorPrinter(vector);
    }

    @Override
    public String print(StringTemplate tpl, ExpressionPrinter expressionPrinter) {
        return getPrinterFor(tpl).print(tpl, expressionPrinter);
    }

    private Printer getPrinterFor(StringTemplate tpl) {
        if (tpl == StringTemplate.editorTemplate) {
            return editPrinter;
        } else if (tpl.isLatex()) {
            return latexPrinter;
        } else {
            return defaultPrinter;
        }
    }
}
