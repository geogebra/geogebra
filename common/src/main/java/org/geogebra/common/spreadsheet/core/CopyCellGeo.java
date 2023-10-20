package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.parser.ParseException;

public class CopyCellGeo {
	private Kernel kernel;
	private StringTemplate precision = StringTemplate.maxPrecision;

	/**
	 * @param value
	 *            copied value
	 * @param oldValue
	 *            overwritten value
	 * @param dx
	 *            column difference
	 * @param dy
	 *            row difference
	 * @param rowStart
	 *            first row
	 * @param columnStart
	 *            first column
	 * @return element copy
	 * @throws ParseException on parse problem
	 * @throws CircularDefinitionException on circular reference
	 */
	public GeoElement copy(
			GeoElement value, GeoElement oldValue, int dx, int dy,
			int rowStart, int columnStart) throws ParseException, CircularDefinitionException {

		String text = getDefinitionOrValue(value);

		// handle GeoText source value
		if (value.isGeoText() && !((GeoText) value).isTextCommand()) {
			// enclose text in quotes if we are copying an independent GeoText,
			// e.g. "2+3"
			if (value.isIndependent()) {
				text = "\"" + text + "\"";
			} else {

				// check if 'text' parses to a GeoText
				GeoText testGeoText = kernel.getAlgebraProcessor()
						.evaluateToText(text, false, false);

				// if it doesn't then force it to by adding +"" on the end
				if (testGeoText == null) {
					text = text + "+\"\"";
				}
			}
		}

		// for E1 = Polynomial[D1] we need value.getCommandDescription();
		// even though it's a GeoFunction
		if (value.isGeoFunction() && "".equals(text)) {
			// we need the definition without A1(x)= on the front
			text = ((GeoFunction) value).toSymbolicString(precision);
		}

		boolean oldFlag = kernel.isUsingInternalCommandNames();
		kernel.setUseInternalCommandNames(true);
		// FIXME maybe try-catch this?
		ValidExpression exp = kernel.getParser().parseGeoGebraExpression(text);
		kernel.setUseInternalCommandNames(oldFlag);

		text = exp.toString(precision);

		// allow pasting blank strings
		if ("".equals(text)) {
			text = "\"\"";
		}

		GeoElementND[] newValues = kernel.getAlgebraProcessor()
				.processAlgebraCommandNoExceptionsOrErrors(text, false);

		return (GeoElement) newValues[0];
	}

	private String getDefinitionOrValue(GeoElement geo) {
		if (geo.isPointOnPath() || geo.isPointInRegion()) {
			return geo.getDefinition(precision);
		}

		if (geo.isChangeable()) {
			return geo.toValueString(precision);
		}

		return geo.getDefinition(precision);
	}
}
