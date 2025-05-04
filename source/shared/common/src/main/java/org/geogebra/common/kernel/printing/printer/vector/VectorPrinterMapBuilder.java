package org.geogebra.common.kernel.printing.printer.vector;

import java.util.Map;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.main.settings.GeneralSettings;

/**
 * Builds vector printer map.
 */
public interface VectorPrinterMapBuilder {

    /**
     * @param settings general settings
     * @return printer map
     */
    Map<VectorPrintingMode, Printer> build(@CheckForNull GeneralSettings settings);
}
