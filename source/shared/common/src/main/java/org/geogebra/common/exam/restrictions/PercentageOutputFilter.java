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

package org.geogebra.common.exam.restrictions;

import org.geogebra.common.gui.view.algebra.filter.AlgebraOutputFilter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Algebra output filter for simple percentage inputs (e.g., {@code 50%},
 * {@code 7%}, {@code 100%}). Compound expressions containing percentages
 * (e.g., {@code 50% + 0.8}) are still allowed.
 */
public class PercentageOutputFilter implements AlgebraOutputFilter {
	@Override
	public boolean isAllowed(GeoElementND element) {
		if (element == null || element.getDefinition() == null) {
			return true;
		}
		ExpressionValue unwrapped = element.getDefinition().unwrap();
		return !(unwrapped instanceof MySpecialDouble
				&& unwrapped.toString(StringTemplate.defaultTemplate).endsWith("%"));
	}
}
