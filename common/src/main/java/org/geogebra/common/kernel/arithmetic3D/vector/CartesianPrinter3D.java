package org.geogebra.common.kernel.arithmetic3D.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.expression.ExpressionPrinter;
import org.geogebra.common.main.settings.GeneralSettings;

class CartesianPrinter3D implements Printer {

	private final GeneralSettings settings;

	public CartesianPrinter3D(GeneralSettings settings) {
		this.settings = settings;
	}

	@Override
	public String print(StringTemplate tpl, ExpressionPrinter expressionPrinter,
			PrintableVector vector) {
		if (tpl.getStringType().isGiac()) {
			boolean vectorNot3dPoint = vector.isCASVector();
			return (vectorNot3dPoint
					? "ggbvect[" : "point(")
					+ expressionPrinter.print(vector.getX(), tpl)
					+ ','
					+ expressionPrinter.print(vector.getY(), tpl)
					+ ','
					+ expressionPrinter.print(vector.getZ(), tpl)
					+ (vectorNot3dPoint ? "]" : ")");
		}
		String delimiter = tpl.getCartesianDelimiter(settings);
		return tpl.leftBracket()
				+ expressionPrinter.print(vector.getX(), tpl)
				+ delimiter
				+ expressionPrinter.print(vector.getY(), tpl)
				+ delimiter
				+ expressionPrinter.print(vector.getZ(), tpl)
				+ tpl.rightBracket();
	}
}
