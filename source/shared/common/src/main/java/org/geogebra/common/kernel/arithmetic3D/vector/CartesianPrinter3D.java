package org.geogebra.common.kernel.arithmetic3D.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.main.settings.GeneralSettings;

class CartesianPrinter3D implements Printer {

	private final GeneralSettings settings;

	public CartesianPrinter3D(GeneralSettings settings) {
		this.settings = settings;
	}

	@Override
	public String print(String xCoord, String yCoord, String zCoord,
			PrintableVector vector, StringTemplate tpl) {
		if (tpl.getStringType().isGiac()) {
			boolean vectorNot3dPoint = vector.isCASVector();
			return (vectorNot3dPoint
					? "ggbvect[" : "point(")
					+ xCoord
					+ ','
					+ yCoord
					+ ','
					+ zCoord
					+ (vectorNot3dPoint ? "]" : ")");
		}
		if (tpl.usePointTemplate()) {
			return "$point("
					+ xCoord
					+ ','
					+ yCoord
					+ ','
					+ zCoord
					+ ')';
		}
		String delimiter = tpl.getCartesianDelimiter(settings);
		return tpl.leftBracket()
				+ xCoord
				+ delimiter
				+ yCoord
				+ delimiter
				+ zCoord
				+ tpl.rightBracket();
	}
}
