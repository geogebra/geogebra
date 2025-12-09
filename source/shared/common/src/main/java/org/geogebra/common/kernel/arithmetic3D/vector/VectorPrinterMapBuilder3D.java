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

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.vector.VectorPrinterMapBuilder;
import org.geogebra.common.kernel.printing.printer.vector.VectorPrintingMode;
import org.geogebra.common.main.settings.GeneralSettings;

public class VectorPrinterMapBuilder3D implements VectorPrinterMapBuilder {

    @Override
    public Map<VectorPrintingMode, Printer> build(@CheckForNull GeneralSettings settings) {
        Map<VectorPrintingMode, Printer> map = new EnumMap<>(VectorPrintingMode.class);
        map.put(VectorPrintingMode.Cartesian, new CartesianPrinter3D(settings));
        map.put(VectorPrintingMode.Polar, new SphericalPrinter());
        map.put(VectorPrintingMode.Vector, new VectorPrinter());
        return map;
    }
}
