package org.geogebra.common.kernel.arithmetic.variable.power;

import java.util.HashMap;
import java.util.Map;

public class Exponents {

	private Map<Base, Integer> exponents;

	public Exponents() {
		exponents = new HashMap<>();
		initWithZero();
	}

	private void initWithZero() {
		exponents.put(Base.x, 0);
		exponents.put(Base.y, 0);
		exponents.put(Base.z, 0);
		exponents.put(Base.theta, 0);
		exponents.put(Base.pi, 0);
	}

	public void increase(Base base) {
		int exponent = exponents.get(base);
		exponent++;
		exponents.put(base, exponent);
	}

	public int get(Base base) {
		return exponents.get(base);
	}
}
