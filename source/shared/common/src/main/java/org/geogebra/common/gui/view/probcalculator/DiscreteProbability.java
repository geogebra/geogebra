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
