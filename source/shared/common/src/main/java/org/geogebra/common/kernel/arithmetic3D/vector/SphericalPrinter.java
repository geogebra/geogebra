package org.geogebra.common.kernel.arithmetic3D.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;

class SphericalPrinter implements Printer {

    @Override
	public String print(String xCoord, String yCoord, String zCoord,
			PrintableVector vector, StringTemplate tpl) {
		if (tpl.getStringType().isGiac()) {
			return "point(("
					+ xCoord
					+ ")*cos("
					+ yCoord
					+ ")*cos("
					+ zCoord
					+ "),("
					+ xCoord
					+ ")*sin("
					+ yCoord
					+ ")*cos("
					+ zCoord
					+ "),("
					+ xCoord
					+ ")*sin("
					+ zCoord
					+ "))";
		}
        return tpl.leftBracket()
                + xCoord
                + "; "
                + yCoord
                + "; "
                + zCoord
                + tpl.rightBracket();
    }
}
