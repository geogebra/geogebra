package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.roots.RealRootFunction;

public class Solution {
	public double[] curRoots = new double[30]; // current roots
	public int curRealRoots;

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

	void removeRoot(int pos) {
		for (int i = pos + 1; i < curRealRoots; i++) {
			curRoots[i - 1] = curRoots[i];
		}
		curRealRoots--;
	}

	public void resetRoots() {
		curRealRoots = 0;
	}

	void ensureSignChanged(RealRootFunction f, double DELTA) {
		double left, right, leftEval, rightEval;
		boolean signUnChanged;
		for (int i = 0; i < curRealRoots; i++) {
			left = curRoots[i] - DELTA;
			right = curRoots[i] + DELTA;
			// ensure we get a non-zero y value to the left
			int count = 0;
			while (Math.abs(leftEval = f.evaluate(left)) < DELTA
					&& count++ < 100)
				left = left - DELTA;

			// ensure we get a non-zero y value to the right
			count = 0;
			while (Math.abs(rightEval = f.evaluate(right)) < DELTA
					&& count++ < 100)
				right = right + DELTA;

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

	public void setSingleRoot(double x) {
		curRoots[0] = x;
		curRealRoots = 1;

	}
}
