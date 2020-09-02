package org.geogebra.common.kernel.arithmetic.vector;

import java.util.EnumMap;
import java.util.Map;

import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.vector.VectorPrinterMapBuilder;
import org.geogebra.common.kernel.printing.printer.vector.VectorPrintingMode;

public class VectorPrinterMapBuilder2D implements VectorPrinterMapBuilder {

    @Override
    public Map<VectorPrintingMode, Printer> build(PrintableVector vector) {
        Map<VectorPrintingMode, Printer> printerMap = new EnumMap<>(VectorPrintingMode.class);
        printerMap.put(VectorPrintingMode.Polar, new PolarPrinter(vector));
        printerMap.put(VectorPrintingMode.Cartesian, new CartesianPrinter(vector));
        printerMap.put(VectorPrintingMode.Vector, new VectorPrinter(vector));
        printerMap.put(VectorPrintingMode.GiacPolar, new GiacPolarPrinter(vector));
        printerMap.put(VectorPrintingMode.Giac, new GiacPrinter(vector));
        printerMap.put(VectorPrintingMode.CasLatex, new CasLatexPrinter(vector));
        return printerMap;
    }
}
