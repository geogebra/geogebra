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

package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.kernel.geos.GeoList;

/**
 * Discrete probability given with the lists of probabilities and values.
 */
public class DiscreteProbability {
	private final GeoList values;
	private final GeoList probabilities;

	/**
	 *
	 * @param values list.
	 * @param probabilities probability list.
	 */
	public DiscreteProbability(GeoList values, GeoList probabilities) {
		this.values = values;
		this.probabilities = probabilities;
	}

	/**
	 *
	 * @return values.
	 */
	public GeoList values() {
		return values;
	}

	/**
	 *
	 * @return probabilities.
	 */
	public GeoList probabilities() {
		return probabilities;
	}
}
