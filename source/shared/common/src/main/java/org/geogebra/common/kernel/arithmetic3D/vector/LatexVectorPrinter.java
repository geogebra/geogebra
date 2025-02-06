package org.geogebra.common.kernel.arithmetic3D.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;

class LatexVectorPrinter implements Printer {

	@Override
	public String print(String xCoord, String yCoord, String zCoord,
			PrintableVector vector, StringTemplate tpl) {
		return printLeftParenthesis(tpl)
				+ xCoord
				+ printDelimiter()
				+ yCoord
				+ printDelimiter()
				+ zCoord
				+ printRightParenthesis(tpl);
	}

	private String printLeftParenthesis(StringTemplate tpl) {
		return tpl.leftBracket() + " \\begin{align}";
	}

	private String printRightParenthesis(StringTemplate tpl) {
		return " \\end{align}" + tpl.rightBracket();
	}

	private String printDelimiter() {
		return " \\\\ ";
	}
}
