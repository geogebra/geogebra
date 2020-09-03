package org.geogebra.common.kernel.arithmetic3D.vector;

import java.util.EnumMap;
import java.util.Map;

import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.vector.VectorPrinterMapBuilder;
import org.geogebra.common.kernel.printing.printer.vector.VectorPrintingMode;

public class VectorPrinterMapBuilder3D implements VectorPrinterMapBuilder {

    @Override
    public Map<VectorPrintingMode, Printer> build(PrintableVector vector) {
        Map<VectorPrintingMode, Printer> map = new EnumMap<>(VectorPrintingMode.class);
        map.put(VectorPrintingMode.Cartesian, new CartesianPrinter(vector));
        map.put(VectorPrintingMode.CasLatex, new CasLatexPrinter(vector));
        map.put(VectorPrintingMode.Polar, new SphericalPrinter(vector));
        map.put(VectorPrintingMode.Giac, new GiacPrinter(vector));
        map.put(VectorPrintingMode.GiacPolar, new GiacSphericalPrinter(vector));
        map.put(VectorPrintingMode.Vector, new VectorPrinter(vector));
        return map;
    }
}
