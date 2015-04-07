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

package org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.geom;

public abstract class FacetShapeCalculator {
	public abstract FacetShape calcShape(double truncPercent);

	static public FacetShapeCalculator lessThan(double compareTruncPercent, FacetShape trueShape, FacetShape falseShape) {
		final double ctp = compareTruncPercent;
		final FacetShape ts = trueShape;
		final FacetShape fs = falseShape;
		return new FacetShapeCalculator() {
			public FacetShape calcShape(double truncPercent) {
				return truncPercent < ctp ? ts : fs;
			}
		};
	}

	static public FacetShapeCalculator greaterThan(double compareTruncPercent, FacetShape trueShape, FacetShape falseShape) {
		final double ctp = compareTruncPercent;
		final FacetShape ts = trueShape;
		final FacetShape fs = falseShape;
		return new FacetShapeCalculator() {
			public FacetShape calcShape(double truncPercent) {
				return truncPercent > ctp ? ts : fs;
			}
		};
	}

	static public FacetShapeCalculator between(double minTruncPercent, double maxTruncPercent, FacetShape trueShape, FacetShape falseShape) {
		final double mintp = minTruncPercent;
		final double maxtp = maxTruncPercent;
		final FacetShape ts = trueShape;
		final FacetShape fs = falseShape;
		return new FacetShapeCalculator() {
			public FacetShape calcShape(double truncPercent) {
				return truncPercent > mintp && truncPercent < maxtp ? ts : fs;
			}
		};
	}

	static public FacetShapeCalculator create(final FacetShape facetShape) {
		return new FacetShapeCalculator() {
			public FacetShape calcShape(double truncPercent) {
				return facetShape;
			}
		};
	}

}
