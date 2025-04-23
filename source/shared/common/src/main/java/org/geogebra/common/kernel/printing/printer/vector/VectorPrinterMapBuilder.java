package org.geogebra.common.kernel.printing.printer.vector;

import java.util.Map;

import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.main.settings.GeneralSettings;

/**
 * Builds vector printer map.
 */
public interface VectorPrinterMapBuilder {

    Map<VectorPrintingMode, Printer> build(GeneralSettings settings);
}
