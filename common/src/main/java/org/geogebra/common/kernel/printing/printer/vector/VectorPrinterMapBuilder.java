package org.geogebra.common.kernel.printing.printer.vector;

import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;

import java.util.Map;

public interface VectorPrinterMapBuilder {

    Map<VectorPrintingMode, Printer> build(PrintableVector vector);
}
