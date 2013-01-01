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

package com.quantimegroup.solutions.archimedean.geom;import com.quantimegroup.solutions.archimedean.utils.Solver;
class MySolver extends Solver {	private double[] v;	private boolean insidePole = true;	private int maxi;	MySolver(int[] polys, double L) {		this(polys, polys.length, L, true);	}// L is the length of a side	MySolver(int[] polys, int nump, double L, boolean inside) {		insidePole = inside;		v = new double[nump];		maxx = L;		double maxv = 0;		for (int i = 0; i < nump; ++i) {			double alpha = Math.PI * (1 - 2.0 / polys[i]);// alpha is a corner of the																										// polygon			v[i] = 2 * L * Math.sin(alpha / 2);// v[i] is length of chord of polygon			if (v[i] > maxv) {				maxv = v[i];				maxi = i;			}		}		minx = maxv / 2;		// minx = 0;	}	public double compute(double r) {		double result = 0;		if (insidePole) {			// the sum of the angles must be 2*pi			for (int i = 0; i < v.length; ++i) {// sum the angles				result += 2 * Math.asin(v[i] / (2 * r));			}			result -= 2 * Math.PI;			//result -= 4 * Math.PI;		} else {			// the largest angle must equal the sum of the other angles			for (int i = 0; i < v.length; ++i) {				if (i == maxi)					continue;				result += 2 * Math.asin(v[i] / (2 * r));			}			result -= 2 * Math.asin(v[maxi] / (2 * r));		}		return result;	}}