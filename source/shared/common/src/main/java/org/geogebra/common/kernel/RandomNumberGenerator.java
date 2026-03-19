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

package org.geogebra.common.kernel;

import java.util.Random;

import org.geogebra.common.util.DoubleUtil;

/**
 * Allows use of seeds to generate the same sequence for a ggb file.
 */
public final class RandomNumberGenerator {

	private Random random = new Random();

	/**
	 * Initialize the random number generator with a new seed.
	 * @param seed the new seed.
	 */
	public void setRandomSeed(int seed) {
		random = new Random(seed);
	}

	/**
	 * @return random number in [0,1).
	 */
	public double getRandomNumber() {
		return random.nextDouble();
	}

	/**
	 * @param a low value of distribution interval
	 * @param b high value of distribution interval
	 * @return random number from Uniform Distribution[a,b]
	 */
	public double randomUniform(double a, double b) {
		return a + getRandomNumber() * (b - a);
	}

	/**
	 * @param low least possible value of result
	 * @param high highest possible value of result
	 * @return random integer between a and b inclusive (or NaN for
	 * getRandomIntegerBetween(5.5, 5.5))
	 */
	public int getRandomIntegerBetween(double low, double high) {
		// make sure 4.000000001 is not rounded up to 5
		double a = DoubleUtil.checkInteger(low);
		double b = DoubleUtil.checkInteger(high);

		// Math.floor/ceil to make sure
		// RandomBetween[3.2, 4.7] is between 3.2 and 4.7
		int min = (int) Math.ceil(Math.min(a, b));
		int max = (int) Math.floor(Math.max(a, b));

		// eg RandomBetween[5.499999, 5.500001]
		// eg RandomBetween[5.5, 5.5]
		if (min > max) {
			int tmp = max;
			max = min;
			min = tmp;
		}

		int bound = max - min + 1;
		// if bound < 0 we have an overflow, guaranteeing -min + 1 >= 1, so min <= 0 is safe to add
		return random.nextInt(bound <= 0 ? Integer.MAX_VALUE : bound) + min;
	}
}
