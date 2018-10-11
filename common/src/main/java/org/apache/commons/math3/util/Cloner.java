package org.apache.commons.math3.util;

import java.util.Arrays;

import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.ode.EquationsMapper;

public class Cloner {
	public static double[] clone(double[] array) {

		// double[] arrayClone = new double[array.length];
		// for (int i = 0; i < array.length; i++) {
		// arrayClone[i] = array[i];
		// }
		// return arrayClone;

		return Arrays.copyOf(array, array.length);
	}

	public static int[] clone(int[] array) {

		// int[] arrayClone = new int[array.length];
		// for (int i = 0; i < array.length; i++) {
		// arrayClone[i] = array[i];
		// }
		// return arrayClone;

		return Arrays.copyOf(array, array.length);

	}

	public static double[][] clone2(double[][] array) {

		double[][] arrayClone = new double[array.length][array[0].length];
		for (int i = 0; i < array.length; i++) {
			arrayClone[i] = clone(array[i]);
		}
		return arrayClone;
	}

	public static Integer[] clone(Integer[] intArray) {

		Integer[] ret = new Integer[intArray.length];

		for (int i = 0; i < intArray.length; i++) {
			ret[i] = intArray[i];
		}

		return ret;
	}

	public static WeightedObservedPoint[] clone(
			WeightedObservedPoint[] points) {
		WeightedObservedPoint[] ret = new WeightedObservedPoint[points.length];

		for (int i = 0; i < points.length; i++) {
			ret[i] = new WeightedObservedPoint(points[i].getWeight(),
					points[i].getX(), points[i].getY());
		}

		return ret;
	}

	public static EquationsMapper[] clone(EquationsMapper[] map) {

		EquationsMapper[] ret = new EquationsMapper[map.length];

		for (int i = 0; i < map.length; i++) {
			ret[i] = new EquationsMapper(map[i].getFirstIndex(),
					map[i].getDimension());
		}

		return ret;
	}
}
