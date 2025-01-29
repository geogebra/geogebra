package org.geogebra.common.kernel.arithmetic3D.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.main.PreviewFeature;

class VectorPrinter implements Printer {

    private Printer defaultPrinter;
    private Printer editPrinter;
    private Printer latexPrinter;

    VectorPrinter() {
        defaultPrinter = new CartesianPrinter3D(null);
        editPrinter = PreviewFeature.isAvailable(PreviewFeature.REALSCHULE_TEMPLATES)
            ? new EditVectorPrinter()
         : (x, y, z, vector, tpl) -> "{{" + x + "}, {" + y + "}, {" + z + "}}";
        latexPrinter = new LatexVectorPrinter();
    }

    @Override
    public String print(String xCoord, String yCoord, String zCoord,
            PrintableVector vector, StringTemplate tpl) {
        return getPrinterFor(tpl).print(xCoord, yCoord, zCoord, vector, tpl);
    }

    private Printer getPrinterFor(StringTemplate tpl) {
        if (tpl.isForEditorParser()) {
            return editPrinter;
        } else if (tpl.isLatex()) {
            return latexPrinter;
        } else {
            return defaultPrinter;
        }
    }
}
