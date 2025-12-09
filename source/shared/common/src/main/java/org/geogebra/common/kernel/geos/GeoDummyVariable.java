/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;

/**
 * Dummy GeoElement to be used for symbolic variable resolving for the GeoGebra
 * CAS.
 * 
 * @see org.geogebra.common.kernel.arithmetic.SymbolicMode
 * @author Markus Hohenwarter
 */
public class GeoDummyVariable extends GeoNumeric {

	private String varName;

	/**
	 * Creates new dummy variable
	 * 
	 * @param c
	 *            construction
	 * @param varName
	 *            variable name
	 */
	public GeoDummyVariable(Construction c, String varName) {
		super(c);
		this.varName = varName;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return tpl.printVariableName(varName);
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(tpl);
	}

	/**
	 * @return variable name
	 */
	public String getVarName() {
		return varName;
	}

	/**
	 * @return GeoElement with same name (null if not found)
	 */
	public GeoElement getElementWithSameName() {
		GeoElement ge = kernel.lookupCasCellLabel(varName);
		if (ge == null) {
			ge = kernel.lookupLabel(varName);
		}
		return ge;
	}

	@Override
	public boolean hasCoords() {
		GeoElement ge = getElementWithSameName();
		if (ge != null && !(ge instanceof GeoDummyVariable)) {
			return ge.hasCoords();
		}
		return false;
	}

	@Override
	public boolean evaluatesToNumber(boolean def) {
		return def;
	}

	@Override
	public boolean isSlider() {
		return false;
	}

	@Override
	public boolean isSliderable() {
		return false;
	}
}
