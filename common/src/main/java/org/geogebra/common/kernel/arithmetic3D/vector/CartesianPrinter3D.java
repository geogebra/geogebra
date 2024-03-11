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
			return (vector.isCASVector() ? "ggbvect[" : "point(")
					+ expressionPrinter.print(vector.getX(), tpl)
					+ ','
					+ expressionPrinter.print(vector.getY(), tpl)
					+ ','
					+ expressionPrinter.print(vector.getZ(), tpl)
					+ (vector.isCASVector() ? "]" : ")");
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
