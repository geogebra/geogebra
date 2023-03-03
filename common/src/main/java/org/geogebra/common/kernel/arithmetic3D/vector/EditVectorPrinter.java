package org.geogebra.common.kernel.arithmetic3D.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.expression.ExpressionPrinter;

class EditVectorPrinter implements Printer {

	@Override
	public String print(StringTemplate tpl, ExpressionPrinter expressionPrinter,
			PrintableVector vector) {
		return printLeftParenthesis()
				+ expressionPrinter.print(vector.getX(), tpl)
				+ printDelimiter()
				+ expressionPrinter.print(vector.getY(), tpl)
				+ printDelimiter()
				+ expressionPrinter.print(vector.getZ(), tpl)
				+ printRightParenthesis();
	}

	private String printLeftParenthesis() {
		return "{{";
	}

	private String printRightParenthesis() {
		return "}}";
	}

	private String printDelimiter() {
		return "}, {";
	}
}
