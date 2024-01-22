package org.geogebra.common.kernel.arithmetic.vector;

import java.util.EnumMap;
import java.util.Map;

import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.vector.VectorPrinterMapBuilder;
import org.geogebra.common.kernel.printing.printer.vector.VectorPrintingMode;
import org.geogebra.common.main.settings.GeneralSettings;

public class VectorPrinterMapBuilder2D implements VectorPrinterMapBuilder {

    @Override
    public Map<VectorPrintingMode, Printer> build(GeneralSettings settings) {
        Map<VectorPrintingMode, Printer> printerMap = new EnumMap<>(VectorPrintingMode.class);
        printerMap.put(VectorPrintingMode.Polar, new PolarPrinter());
        printerMap.put(VectorPrintingMode.Cartesian, new CartesianPrinter(settings));
        printerMap.put(VectorPrintingMode.Vector, new VectorPrinter());
        return printerMap;
    }
}
