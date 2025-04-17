package org.geogebra.common.kernel.printing.printer;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;

public interface Printer {

    /**
     * Serializes a vector to a string.
     * @param xCoord vector's x-coordinate
     * @param yCoord vector's y-coordinate
     * @param zCoord vector's z-coordinate
     * @param vector vector object
     * @param tpl template
     * @return serialized vector
     */
    String print(String xCoord, String yCoord, String zCoord,
            PrintableVector vector, StringTemplate tpl);
}
