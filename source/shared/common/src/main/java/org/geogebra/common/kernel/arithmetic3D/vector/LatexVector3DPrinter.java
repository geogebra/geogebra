package org.geogebra.common.kernel.arithmetic3D.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.vector.LatexVectorPrinter;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;

class LatexVector3DPrinter implements Printer {

	@Override
	public String print(String xCoord, String yCoord, String zCoord,
			PrintableVector vector, StringTemplate tpl) {
		return LatexVectorPrinter.printLaTeXVector(new StringBuilder(),
				xCoord, yCoord, zCoord);
	}
}
