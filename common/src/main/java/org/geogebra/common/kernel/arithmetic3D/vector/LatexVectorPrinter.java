package org.geogebra.common.kernel.arithmetic3D.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.expression.ExpressionPrinter;

class LatexVectorPrinter implements Printer {

	private PrintableVector vector;

	LatexVectorPrinter(PrintableVector vector) {
		this.vector = vector;
	}

	@Override
	public String print(StringTemplate tpl, ExpressionPrinter expressionPrinter) {
		return printLeftParenthesis(tpl)
				+ expressionPrinter.print(vector.getX(), tpl)
				+ printDelimiter()
				+ expressionPrinter.print(vector.getY(), tpl)
				+ printDelimiter()
				+ expressionPrinter.print(vector.getZ(), tpl)
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
