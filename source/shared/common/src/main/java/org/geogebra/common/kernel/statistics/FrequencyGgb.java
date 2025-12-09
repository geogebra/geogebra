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

package org.geogebra.common.kernel.statistics;

import java.util.Comparator;

import org.apache.commons.math3.stat.Frequency;
import org.geogebra.common.util.DoubleUtil;

/**
 * Extension of the Apache Commons Frequency class. Modifies the addValue()
 * method to handle comparisons with double values that may contain rounding
 * errors but should be treated as equal.
 * 
 * @author G. Sturr
 * 
 */
public class FrequencyGgb extends Frequency {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a frequency object with rounding comparator.
	 */
	public FrequencyGgb() {
		super((Comparator<Double>) (o1, o2) -> {
			if (DoubleUtil.isEqual(o1, o2)) {
				return 0;
			}
			return o2 > o1 ? -1 : 1;
		});
	}

	@Override
	public void addValue(Comparable<?> v) {
		if (v instanceof Double) {
			Double v2 = DoubleUtil.checkDecimalFraction((Double) v);
			super.addValue(v2);
			return;
		}
		super.addValue(v);
	}
}
