package geogebra.common.main;

import geogebra.common.kernel.Kernel;
import geogebra.common.util.MyMath2;

import java.util.Random;

/**
 * Collection of utility methods to handle random numbers.
 * 
 * @author G. Sturr
 */
public class RandomUtil {

	private static Random random = new Random();

	/**
	 * allows use of seeds to generate the same sequence for a ggb file
	 * 
	 * @return random number in [0,1]
	 */
	public static double getRandomNumber() {
		return random.nextDouble();
	}

	/**
	 * allows use of seeds to generate the same sequence for a ggb file
	 * 
	 * @param seed
	 *            new seed
	 */
	public void setRandomSeed(int seed) {
		random = new Random(seed);
	}

	/**
	 * allows use of seeds to generate the same sequence for a ggb file
	 * 
	 * @param low
	 *            least possible value of result
	 * @param high
	 *            highest possible value of result
	 * 
	 * @return random integer between a and b inclusive
	 * 
	 */
	public static int getRandomIntegerBetween(double low, double high) {
		// make sure 4.000000001 is not rounded up to 5
		double a = Kernel.checkInteger(low);
		double b = Kernel.checkInteger(high);

		// Math.floor/ceil to make sure
		// RandomBetween[3.2, 4.7] is between 3.2 and 4.7
		int min = (int) Math.ceil(Math.min(a, b));
		int max = (int) Math.floor(Math.max(a, b));

		return random.nextInt(max - min + 1) + min;

	}

	/**
	 * @param a
	 *            low value of distribution interval
	 * @param b
	 *            high value of distribution interval
	 * @return random number from Uniform Distribution[a,b]
	 */
	public static double randomUniform(double a, double b) {
		return a + getRandomNumber() * (b - a);
	}

	/**
	 * @param n
	 *            number of trials
	 * @param p
	 *            probability of success
	 * @return random number from Binomial Distribution[n,p]
	 */
	public static int randomBinomial(double n, double p) {

		int count = 0;
		for (int i = 0; i < n; i++) {
			if (getRandomNumber() < p)
				count++;
		}

		return count;

	}

	/**
	 * @param mean
	 * @param sd
	 *            standard deviation
	 * @return random number from Binomial Distribution[mean,sd]
	 */
	public static double randomNormal(double mean, double sd) {
		double fac, rsq, v1, v2;
		do {
			v1 = 2.0 * getRandomNumber() - 1;
			v2 = 2.0 * getRandomNumber() - 1; // two random numbers from -1 to
												// +1
			rsq = v1 * v1 + v2 * v2;
		} while (rsq >= 1.0 || rsq == 0.0); // keep going until they are in the
											// unit circle
		fac = Math.sqrt(-2.0 * Math.log(rsq) / rsq);
		// Application.debug("randomNormal="+(v1*fac));
		return v1 * fac * sd + mean;

	}

	/**
	 * Poisson random number (Knuth)
	 * 
	 * @param lambda
	 * @return random number from Poisson Distribution[lambda]
	 */
	private static int randomPoisson(double lambda) {
		double L = Math.exp(-lambda);
		double p = 1;
		int k = 0;
		do {
			k++;
			p *= getRandomNumber();
		} while (p >= L);

		return k - 1;

	}

	/*
	 * 
	 * H�rmann, Wolfgang: The transformed rejection method for generating
	 * Poisson random variables Algorithm PTRS
	 * http://statmath.wu-wien.ac.at/papers/92-04-13.wh.ps.gz
	 * http://epub.wu-wien
	 * .ac.at/dyn/virlib/wp/eng/mediate/epub-wu-01_6f2.pdf?ID=epub-wu-01_6f2
	 */
	public static int randomPoissonTRS(double mu) {

		if (mu < 10)
			return randomPoisson(mu);

		double b = 0.931 + +2.53 * Math.sqrt(mu);
		double a = -0.059 + 0.02438 * b;
		double v_r = 0.9277 - 3.6224 / (b - 2);

		double us = 0;
		double v = 1;

		while (true) {

			int k = -1;
			while (k < 0 || (us < 0.013 && v > us)) {
				double u = getRandomNumber() - 0.5;
				v = getRandomNumber();
				us = 0.5 - Math.abs(u);
				k = (int) Math.floor((2 * a / us + b) * u + mu + 0.43);
				if (us >= 0.07 && v < v_r)
					return k;
			}

			double alpha = 1.1239 + 1.1328 / (b - 3.4);
			double lnmu = Math.log(mu);

			v = v * alpha / (a / (us * us) + b);

			if (Math.log(v * alpha / (a / us / us + b)) <= -mu + k * lnmu
					- logOfKFactorial(k))
				return k;
		}

	}

	private static double halflog2pi = 0.5 * Math.log(2 * Math.PI);
	private static double logtable[] = new double[10];

	private static double logOfKFactorial(int k) {
		if (k < 10) {
			if (logtable[k] == 0)
				logtable[k] = Math.log(MyMath2.factorial(k));
			return logtable[k];
		}

		// Stirling approximation
		return halflog2pi
				+ (k + 0.5)
				* Math.log(k + 1)
				- (k + 1)
				+ (1 / 12.0 - (1 / 360.0 - 1 / 1260.0 / (k + 1) / (k + 1))
						/ (k + 1) / (k + 1)) / (k + 1);
	}

}
