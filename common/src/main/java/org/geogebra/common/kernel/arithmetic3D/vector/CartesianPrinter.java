package org.geogebra.common.kernel.arithmetic3D.vector;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.expression.ExpressionPrinter;
import org.geogebra.common.main.settings.GeneralSettings;

class CartesianPrinter implements Printer {

	private final GeneralSettings settings;

	public CartesianPrinter(GeneralSettings settings) {
		this.settings = settings;
	}

	@Override
    public String print(StringTemplate tpl, ExpressionPrinter expressionPrinter,
			PrintableVector vector) {
		String delimiter = settings.getCoordFormat() == Kernel.COORD_STYLE_AUSTRIAN
				? (tpl.getOptionalSpace() + tpl.getPointCoordBar()) : ",";
        return tpl.leftBracket()
                + expressionPrinter.print(vector.getX(), tpl)
                + delimiter
                + tpl.getOptionalSpace()
                + expressionPrinter.print(vector.getY(), tpl)
                + delimiter
                + tpl.getOptionalSpace()
                + expressionPrinter.print(vector.getZ(), tpl)
                + tpl.rightBracket();
    }
}
