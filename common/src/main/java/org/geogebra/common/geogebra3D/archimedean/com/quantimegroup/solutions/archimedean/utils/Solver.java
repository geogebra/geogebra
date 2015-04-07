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

public abstract class Solver {
	protected double minx;
	protected double maxx;

	public double solve() {
		return solve(0);
	}

	public double solve(double e) {
		// y = f(x); f(x) must be continous and have no local maximums or minimums
		// within the range minx to maxx
		int inverse = compute(maxx) < compute(minx) ? -1 : 1;
		double x = maxx, y;
		do {
			double newx = (minx + maxx) / 2;
			if (newx == x)
				break;
			x = newx;
			y = compute(x) * inverse;
			// System.out.println( " x = " + x + " y = " + y );
			if (y > 0)
				maxx = x;
			else
				minx = x;
		} while (Math.abs(y) > e);
		return x;
	}

	public abstract double compute(double x);
}
