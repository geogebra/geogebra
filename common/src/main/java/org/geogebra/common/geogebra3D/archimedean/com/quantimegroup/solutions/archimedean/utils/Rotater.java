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

package org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils;public class Rotater extends Quick3X3Matrix {	private Quick3X3Matrix P, N;	public Rotater(OrderedTriple axisOfRotation, double theta) {		OrderedTriple p0, p1, p2, n0, n1 = null, n2;		p0 = n0 = axisOfRotation;		p1 = p0.arbitraryPerpendicular().unit().times(p0.length());		try {			n1 = OrderedTriple.findThirdVector(axisOfRotation, p1, Math.PI / 2, theta, p0.cross(p1));		} catch (Exception e) {			System.out.println(e.getMessage());		}		p2 = p1.cross(p0);		n2 = n1.cross(n0);		P = new Quick3X3Matrix(p0, p1, p2);		N = new Quick3X3Matrix(n0, n1, n2);		// mat = N.dividedBy( P ).mat;		mat = N.times(P.inverse()).mat;	}	public void update(Quick3X3Matrix M) {		P = M.times(P);		N = M.times(N);		// mat = N.dividedBy( P ).mat;		mat = N.times(P.inverse()).mat;	}	static public double deg2Rad(double d) {		return (d / 180) * Math.PI;	}	static public double rad2Deg(double r) {		return (r / Math.PI) * 180;	}	/*	 * public String toString(){ return P.toString() +	 * "\n--------------------------\n" + N.toString(); }	 */}