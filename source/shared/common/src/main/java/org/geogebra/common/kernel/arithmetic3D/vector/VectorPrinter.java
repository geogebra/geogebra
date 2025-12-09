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

package org.geogebra.common.kernel.arithmetic3D.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;

class VectorPrinter implements Printer {

    private Printer defaultPrinter;
    private Printer editPrinter;
    private Printer latexPrinter;

    VectorPrinter() {
        defaultPrinter = new CartesianPrinter3D(null);
        editPrinter = new EditVectorPrinter();
        latexPrinter = new LatexVector3DPrinter();
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
