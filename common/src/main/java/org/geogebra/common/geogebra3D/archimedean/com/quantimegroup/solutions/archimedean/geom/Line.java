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

import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.OrderedTriple;

public class Line {
	private OrderedTriple point;
	private OrderedTriple vector;

	protected Line(OrderedTriple p, OrderedTriple v) {
		this.point = p;
		this.vector = v;
	}

	static public Line fromTwoPoints(OrderedTriple p1, OrderedTriple p2) {
		return new Line(p1, p1.minus(p2).unit());
	}

	public boolean same(Line l) {
		OrderedTriple unit = vector.unit();
		OrderedTriple lunit = l.vector.unit();
		if (!unit.equals(lunit) && !unit.equals(lunit.negative())) {
			return false;
		}
		if (point.equals(l.point)) {
			return true;
		}
		OrderedTriple pDiff = point.minus(l.point).unit();
		return pDiff.equals(unit) || pDiff.equals(unit.negative());
	}

	public OrderedTriple getPoint() {
		return point;
	}

	public OrderedTriple getVector() {
		return vector;
	}

}
