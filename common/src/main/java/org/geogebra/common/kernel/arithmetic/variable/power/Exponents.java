package org.geogebra.common.kernel.arithmetic.variable.power;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the exponents of the expression's variables and constants.
 */
public class Exponents {

	private Map<String, Integer> exponentMap;

	/**
	 * Initializes the exponents with a new Map and sets every exponent to zero.
	 */
	public Exponents() {
		exponentMap = new HashMap<>();
	}

	/**
	 * Sets every exponent to zero.
	 */
	public void initWithZero() {
		exponentMap.clear();
	}

	/**
	 * Increases the exponent of a base by one.
	 * @param base The base on which the exponent is increased.
	 */
	public void increase(String base) {
		int exponent = get(base) + 1;
		exponentMap.put(base, exponent);
	}

	/**
	 * @param base The exponent of this base will be returned.
	 * @return The exponent of a base.
	 */
	public int get(String base) {
		Integer exponentOrNull = exponentMap.get(base);
		return exponentOrNull == null ? 0 : exponentOrNull;
	}
}
