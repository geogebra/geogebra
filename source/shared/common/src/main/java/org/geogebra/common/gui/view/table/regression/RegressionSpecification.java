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

package org.geogebra.common.gui.view.table.regression;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.MyVecNode;

public interface RegressionSpecification {

	/**
	 * @return label for selection dropdown
	 */
	String getLabel();

	/**
	 * @param kernel kernel
	 * @param points input data as a tuple (x-coordinates, y-coordinates)
	 * @return regression command
	 */
	Command buildCommand(Kernel kernel, MyVecNode points);

	/**
	 * @return LaTeX formula of the model, may be null if we don't want to show it
	 */
	@CheckForNull String getFormula();

	/**
	 * @return ordered coefficient names (concatenated)
	 */
	String getCoeffOrdering();

	/**
	 * @return whether correlation coefficient should be shown
	 */
	boolean hasCorrelationCoefficient();

	/**
	 * @return whether we're allowed to plot this in the graphics view
	 */
	boolean canPlot();

	/**
	 * @return whether coefficient of determination is allowed to be shown
	 */
	boolean hasCoefficientOfDetermination();

	/**
	 * @param i coefficient index within the formula
	 * @return coefficient name
	 */
	default char getCoeffName(int i) {
		return (char) ('a' + i);
	}
}
