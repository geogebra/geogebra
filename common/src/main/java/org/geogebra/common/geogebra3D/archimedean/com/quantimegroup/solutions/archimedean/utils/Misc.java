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

package org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils;

import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.geom.IFacet;

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
			int[] copy = clone(a);
			System.arraycopy(a, 0, a, d, a.length - d);
			System.arraycopy(copy, a.length - d, a, 0, d);
		} else {
			d *= -1;
			d %= a.length;
			int[] copy = clone(a);
			System.arraycopy(a, d, a, 0, a.length - d);
			System.arraycopy(copy, 0, a, a.length - d, d);

		}
	}
	
	final public static int[] clone(int[] a){
		int[] ret = new int[a.length];
		for (int index = 0 ; index < a.length ; index++){
			ret[index] = a[index];
		}
		
		return ret;
	}

	public static void arrayReverse(int[] a) {
		for (int i = 0; i < a.length / 2; ++i) {
			int temp = a[i];
			a[i] = a[a.length - i - 1];
			a[a.length - i - 1] = temp;
		}

	}

	public static void arrayReverse(Object[] a) {
		for (int i = 0; i < a.length / 2; ++i) {
			Object temp = a[i];
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

	public static String joinStrings(String[] strings, String joinString) {
		return joinStrings(strings, 0, strings.length, joinString);
	}

	public static String joinStrings(String[] strings, int beginIndex, int endIndex, String joinString) {
		if (endIndex - beginIndex <= 0) {
			return "";
		}
		StringBuffer buf = new StringBuffer(strings[beginIndex]);
		for (int i = beginIndex + 1; i < endIndex; ++i) {
			buf.append(joinString);
			buf.append(strings[i]);
		}
		return buf.toString();
	}

	/*
	public static <K> K[] addArrays(K[] a, K[] b) {
		K[] c = (K[]) Array.newInstance(a.getClass().getComponentType(), a.length + b.length);
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}
	*/

	public static double clamp(double value, double min, double max) {
		if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		} else {
			return value;
		}
	}

	public static int countOccurences(int[] array, int value) {
		int count = 0;
		for (int i : array) {
			if (i == value) {
				count++;
			}
		}
		return count;
	}

	public static boolean betweenExclusive(double value, double rangeStart, double rangeEnd) {
		if (rangeEnd < rangeStart) {
			double temp = rangeStart;
			rangeStart = rangeEnd;
			rangeEnd = temp;
		}
		return value > rangeStart && value < rangeEnd;
	}

	public static boolean betweenInclusive(double value, double rangeStart, double rangeEnd, double epsilon) {
		if (rangeEnd < rangeStart) {
			double temp = rangeStart;
			rangeStart = rangeEnd;
			rangeEnd = temp;
		}
		return (value >= rangeStart && value <= rangeEnd) || OrderedTriple.isApprox(value, rangeStart, epsilon)
				|| OrderedTriple.isApprox(value, rangeEnd, epsilon);
	}

	public static boolean betweenInclusive(double value, double rangeStart, double rangeEnd) {
		if (rangeEnd < rangeStart) {
			double temp = rangeStart;
			rangeStart = rangeEnd;
			rangeEnd = temp;
		}
		return value >= rangeStart && value <= rangeEnd;
	}

	public static OrderedTriple[] threeDistinctPoints(IFacet facet) {
		// assumes that there are three distinct points
		OrderedTriple[] p = new OrderedTriple[3];
		double epsilon = 1e-10;
		//double epsilon = 1e-8;

		p[0] = facet.getPoint(0);
		int i;
		for (i = 1; i < facet.getVertexCount(); ++i) {
			p[1] = facet.getPoint(i);
			if (!p[1].isApprox(p[0], epsilon))
				break;
		}
		for (i = i + 1; i < facet.getVertexCount(); ++i) {
			p[2] = facet.getPoint(i);
			if (!p[2].isApprox(p[1], epsilon) && !p[2].isApprox(p[0], epsilon))
				break;
		}
		if(p[0] == null || p[1] == null || p[2] == null){
			throw new NullPointerException();
		}
		return p;
	}

}
