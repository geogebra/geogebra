package org.apache.commons.math3.util;

import java.util.Arrays;

import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.ode.EquationsMapper;

/**
 * Helper class for cloning arrays (there is no clone method in GWT).
 */
public class Cloner {
	/**
	 * @param array
	 *            original array
	 * @return clone of the array
	 */
	public static double[] clone(double[] array) {
		return Arrays.copyOf(array, array.length);
	}

	/**
	 * @param original
	 *            original array
	 * @param target
	 *            target array
	 */
	public static void cloneTo(double[] original, double[] target) {
		System.arraycopy(original, 0, target, 0, original.length);
	}

	/**
	 * @param array
	 *            original array
	 * @return clone of the array
	 */
	public static int[] clone(int[] array) {
		return Arrays.copyOf(array, array.length);
	}

	/**
	 * @param array
	 *            original array
	 * @return clone of two-dimensional array
	 */
	public static double[][] clone2(double[][] array) {
		double[][] arrayClone = new double[array.length][array[0].length];
		for (int i = 0; i < array.length; i++) {
			arrayClone[i] = clone(array[i]);
		}
		return arrayClone;
	}

	/**
	 * @param intArray
	 *            original array
	 * @return clone of the array
	 */
	public static Integer[] clone(Integer[] intArray) {
		return Arrays.copyOf(intArray, intArray.length);
	}

	/**
	 * @param points
	 *            weighted points
	 * @return new array of cloned weighted points
	 */
	public static WeightedObservedPoint[] clone(
			WeightedObservedPoint[] points) {
		WeightedObservedPoint[] ret = new WeightedObservedPoint[points.length];

		for (int i = 0; i < points.length; i++) {
			ret[i] = new WeightedObservedPoint(points[i].getWeight(),
					points[i].getX(), points[i].getY());
		}

		return ret;
	}

	/**
	 * @param map
	 *            equation mappers
	 * @return new array of cloned equation mappers
	 */
	public static EquationsMapper[] clone(EquationsMapper[] map) {
		EquationsMapper[] ret = new EquationsMapper[map.length];

		for (int i = 0; i < map.length; i++) {
			ret[i] = new EquationsMapper(map[i].getFirstIndex(),
					map[i].getDimension());
		}

		return ret;
	}
}
