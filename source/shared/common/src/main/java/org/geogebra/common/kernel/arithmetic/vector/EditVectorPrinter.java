package org.geogebra.common.kernel.arithmetic.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;

class EditVectorPrinter implements Printer {

	@Override
	public String print(String xCoord,
			String yCoord, String zCoord, PrintableVector vector, StringTemplate tpl) {
		return "$vector("
				+ xCoord
				+ ","
				+ yCoord
				+ ")";
	}
}
