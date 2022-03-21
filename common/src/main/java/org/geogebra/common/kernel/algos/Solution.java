package org.geogebra.common.kernel.algos;

import java.util.Arrays;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.Kernel;

/**
 * Set of real numbers that are solutions to an equation
 */
public class Solution {
	/** roots */
	public double[] curRoots = new double[30]; // current roots
	/** number of real roots */
	public int curRealRoots;

	/**
	 * 
	 * @param roots
	 *            roots to be added
	 * @param number
	 *            number of roots to be added
	 */
	public void addToCurrentRoots(double[] roots, int number) {
		int length = curRealRoots + number;
		if (length >= curRoots.length) { // ensure space
			double[] temp = new double[2 * length];
			for (int i = 0; i < curRealRoots; i++) {
				temp[i] = curRoots[i];
			}
			curRoots = temp;
		}

		// insert new roots
		for (int i = 0; i < number; i++) {
			curRoots[curRealRoots + i] = roots[i];
		}
		curRealRoots += number;
	}

	/**
	 * Removed root at given position
	 * 
	 * @param pos
	 *            position
	 */
	void removeRoot(int pos) {
		for (int i = pos + 1; i < curRealRoots; i++) {
			curRoots[i - 1] = curRoots[i];
		}
		curRealRoots--;
	}

	/**
	 * Remove all roots (reset the counter)
	 */
	public void resetRoots() {
		curRealRoots = 0;
	}

	/**
	 * Remove roots where sign didn't change
	 * 
	 * @param f
	 *            function
	 * @param DELTA
	 *            half-width of the interval where we check sign
	 */
	void ensureSignChanged(UnivariateFunction f, double DELTA) {
		double left, right, leftEval, rightEval;
		boolean signUnChanged;
		for (int i = 0; i < curRealRoots; i++) {
			left = curRoots[i] - DELTA;
			right = curRoots[i] + DELTA;
			// ensure we get a non-zero y value to the left
			int count = 0;
			while (Math.abs(leftEval = f.value(left)) < DELTA
					&& count++ < 100) {
				left = left - DELTA;
			}

			// ensure we get a non-zero y value to the right
			count = 0;
			while (Math.abs(rightEval = f.value(right)) < DELTA
					&& count++ < 100) {
				right = right + DELTA;
			}

			// Application.debug("leftEval: " + leftEval + ", left: " + left);
			// Application.debug("rightEval: " + rightEval + ", right: " +
			// right);

			// check if the second derivative changed its sign here
			signUnChanged = leftEval * rightEval > 0;
			if (signUnChanged) {
				// remove root[i]
				removeRoot(i);
				i--;
			}
		}
	}

	/**
	 * @param x
	 *            single root
	 */
	public void setSingleRoot(double x) {
		curRoots[0] = x;
		curRealRoots = 1;

	}

	/**
	 * Sort roots and eliminate duplicate ones
	 */
	public void sortAndMakeUnique() {
		// sort roots and eliminate duplicate ones
		Arrays.sort(this.curRoots, 0, this.curRealRoots);

		// eliminate duplicate roots
		double maxRoot = this.curRoots[0];
		int maxIndex = 0;
		for (int i = 1; i < this.curRealRoots; i++) {
			if ((this.curRoots[i] - maxRoot) > Kernel.MIN_PRECISION) {
				maxRoot = this.curRoots[i];
				maxIndex++;
				this.curRoots[maxIndex] = maxRoot;
			}
		}
		this.curRealRoots = maxIndex + 1;
	}
}
