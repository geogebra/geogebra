package org.geogebra.common.kernel.arithmetic.variable.power;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the exponents of the expression's variables and constants.
 */
public class Exponents {

	private Map<Base, Integer> exponents;

	/**
	 * Initializes the exponents with a new Map and sets every exponent to zero.
	 */
	public Exponents() {
		exponents = new HashMap<>();
		initWithZero();
	}

	/**
	 * Sets every exponent to zero.
	 */
	public void initWithZero() {
		exponents.put(Base.x, 0);
		exponents.put(Base.y, 0);
		exponents.put(Base.z, 0);
		exponents.put(Base.theta, 0);
		exponents.put(Base.pi, 0);
	}

	/**
	 * Increases the exponent of a base by one.
	 * @param base The base on which the exponent is increased.
	 */
	public void increase(Base base) {
		int exponent = exponents.get(base);
		exponent++;
		exponents.put(base, exponent);
	}

	/**
	 * @param base The exponent of this base will be returned.
	 * @return The exponent of a base.
	 */
	public int get(Base base) {
		return exponents.get(base);
	}
}
