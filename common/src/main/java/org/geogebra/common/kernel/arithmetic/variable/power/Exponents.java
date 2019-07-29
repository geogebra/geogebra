package org.geogebra.common.kernel.arithmetic.variable.power;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the exponents of the expression's variables and constants.
 */
public class Exponents {

	private Map<Base, Integer> exponentMap;

	/**
	 * Initializes the exponents with a new Map and sets every exponent to zero.
	 */
	public Exponents() {
		exponentMap = new HashMap<>();
		initWithZero();
	}

	/**
	 * Sets every exponent to zero.
	 */
	public void initWithZero() {
		exponentMap.put(Base.x, 0);
		exponentMap.put(Base.y, 0);
		exponentMap.put(Base.z, 0);
		exponentMap.put(Base.theta, 0);
		exponentMap.put(Base.t, 0);
		exponentMap.put(Base.pi, 0);
	}

	/**
	 * Increases the exponent of a base by one.
	 * @param base The base on which the exponent is increased.
	 */
	public void increase(Base base) {
		int exponent = exponentMap.get(base);
		exponent++;
		exponentMap.put(base, exponent);
	}

	/**
	 * @param base The exponent of this base will be returned.
	 * @return The exponent of a base.
	 */
	public int get(Base base) {
		return exponentMap.get(base);
	}
}
