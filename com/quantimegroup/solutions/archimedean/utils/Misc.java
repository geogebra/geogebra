/*
Archimedean 1.1, a 3D applet/application for visualizing, building, 
transforming and analyzing Archimedean solids and their derivatives.
Copyright 1998, 2011 Raffi J. Kasparian, www.raffikasparian.com.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.quantimegroup.solutions.archimedean.utils;

public class Misc {
	private Misc() {
	}

	static public int arrayCompare(int[] a, int[] b) {
		if (a.length < b.length)
			return -1;
		if (a.length > b.length)
			return 1;
		for (int i = 0; i < a.length; ++i) {
			if (a[i] < b[i])
				return -1;
			if (a[i] > b[i])
				return 1;
		}
		return 0;
	}

	static public void arrayRotate(int[] a, int d) {
		if (d >= 0) {
			d %= a.length;
			if (d == 0) {
				return;
			}
			int[] copy = (int[]) a.clone();
			System.arraycopy(a, 0, a, d, a.length - d);
			System.arraycopy(copy, a.length - d, a, 0, d);
		} else {
			d *= -1;
			d %= a.length;
			int[] copy = (int[]) a.clone();
			System.arraycopy(a, d, a, 0, a.length - d);
			System.arraycopy(copy, 0, a, a.length - d, d);

		}
	}

	public static void arrayReverse(int[] a) {
		for (int i = 0; i < a.length / 2; ++i) {
			int temp = a[i];
			a[i] = a[a.length - i - 1];
			a[a.length - i - 1] = temp;
		}

	}

	public static double round(double d, int mantissaLength) {
		for (int i = 0; i < mantissaLength; ++i) {
			d *= 10;
		}
		d = Math.round(d);
		for (int i = 0; i < mantissaLength; ++i) {
			d /= 10;
		}
		return d;
	}

}
