package org.geogebra.common.kernel.printing.printer.vector;

import java.util.Map;

import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;

public interface VectorPrinterMapBuilder {

    Map<VectorPrintingMode, Printer> build(PrintableVector vector);
}
