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

package org.geogebra.common.kernel.arithmetic;

import java.math.BigDecimal;

import org.geogebra.common.kernel.GeoElementConvertible;
import org.geogebra.common.kernel.StringTemplate;

/**
 * Interface for elements with numeric value (numerics, segments, polygons, ...)
 * and their counterparts from geogebra.common.kernel.arithmetic (MyDouble)
 * 
 * @author Markus
 */
public interface NumberValue extends ExpressionValue, GeoElementConvertible {
	/**
	 * @return MyDouble whose value equals #getDouble()
	 */
	public MyDouble getNumber();

	/**
	 * 
	 * @return true for angles
	 */
	public int getAngleDim();

	/**
	 * 
	 * @return value of this number
	 */
	public double getDouble();

	/**
	 * @return whether this value is defined or not
	 */
	public boolean isDefined();

	/**
	 * @param tpl
	 *            output template
	 * @return label for GeoElements, value for MyDouble
	 */
	public String getLabel(StringTemplate tpl);

	/**
	 * Might be null if this is a constant +-infinity or NaN,
	 * also for results of imprecise computations
	 * @return this as big decimal
	 */
	default BigDecimal toDecimal() {
		return null;
	}
}
