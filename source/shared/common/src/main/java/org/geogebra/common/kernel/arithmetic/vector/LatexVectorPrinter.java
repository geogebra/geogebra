package org.geogebra.common.kernel.arithmetic.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.expression.ExpressionPrinter;

class LatexVectorPrinter implements Printer {

	@Override
	public String print(StringTemplate tpl, ExpressionPrinter expressionPrinter,
			PrintableVector vector) {
		return printLeftParenthesis(tpl)
				+ expressionPrinter.print(vector.getX(), tpl)
				+ printDelimiter()
				+ expressionPrinter.print(vector.getY(), tpl)
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
