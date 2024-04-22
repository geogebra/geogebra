package org.geogebra.common.kernel.arithmetic3D.vector;

import java.util.EnumMap;
import java.util.Map;

import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.vector.VectorPrinterMapBuilder;
import org.geogebra.common.kernel.printing.printer.vector.VectorPrintingMode;
import org.geogebra.common.main.settings.GeneralSettings;

public class VectorPrinterMapBuilder3D implements VectorPrinterMapBuilder {

    @Override
    public Map<VectorPrintingMode, Printer> build(GeneralSettings settings) {
        Map<VectorPrintingMode, Printer> map = new EnumMap<>(VectorPrintingMode.class);
        map.put(VectorPrintingMode.Cartesian, new CartesianPrinter3D(settings));
        map.put(VectorPrintingMode.Polar, new SphericalPrinter());
        map.put(VectorPrintingMode.Vector, new VectorPrinter());
        return map;
    }
}
