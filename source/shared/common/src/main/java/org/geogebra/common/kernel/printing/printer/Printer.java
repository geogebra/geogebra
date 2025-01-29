package org.geogebra.common.kernel.printing.printer;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;

public interface Printer {

    String print(String xCoord, String yCoord, String zCoord,
            PrintableVector vector, StringTemplate tpl);
}
