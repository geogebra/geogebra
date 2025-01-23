package org.geogebra.common.util;

import org.geogebra.common.kernel.geos.GeoElement;

// Eyal Schneider
// http://stackoverflow.com/a/2799190
/**
 * Utility Class to permute the array of GeoElements
 * 
 * @author Eyal Schneider, http://stackoverflow.com/a/2799190
 * @author Adaption: Christoph Stadlbauer
 */
class PermutationOfGeOElementsUtil {
	private GeoElement[] arr;
	private int[] permSwaps;

	/**
	 * @param arr
	 *            the Array with the Elements to be permuted
	 */
	public PermutationOfGeOElementsUtil(GeoElement[] arr) {
		this(arr, arr.length);
	}

	/**
	 * @param arr
	 *            the Array with the Elements to be permuted
	 * @param permSize
	 *            the Elements k < arr.length of the array you need to permute
	 */
	public PermutationOfGeOElementsUtil(GeoElement[] arr, int permSize) {

		// this.arr = arr.clone();
		this.arr = new GeoElement[arr.length];
		System.arraycopy(arr, 0, this.arr, 0, arr.length);
		this.permSwaps = new int[permSize];
		for (int i = 0; i < permSwaps.length; i++) {
			permSwaps[i] = i;
		}
	}

	/**
	 * @return the next permutation of the array if exists, null otherwise
	 */
	public GeoElement[] next() {
		if (arr == null) {
			return null;
		}

		GeoElement[] res = new GeoElement[permSwaps.length];
		System.arraycopy(arr, 0, res, 0, permSwaps.length);

		// Prepare next
		int i = permSwaps.length - 1;
		while (i >= 0 && permSwaps[i] == arr.length - 1) {
			swap(i, permSwaps[i]); // Undo the swap represented by
										// permSwaps[i]
			permSwaps[i] = i;
			i--;
		}

		if (i < 0) {
			arr = null;
		} else {
			int prev = permSwaps[i];
			swap(i, prev);
			int next = prev + 1;
			permSwaps[i] = next;
			swap(i, next);
		}

		return res;
	}

	private void swap(int i, int j) {
		GeoElement tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
	}

}