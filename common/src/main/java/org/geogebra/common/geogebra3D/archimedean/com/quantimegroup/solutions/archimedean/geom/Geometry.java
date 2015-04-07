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

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.ObjectList;
import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.OrderedTriple;

public class Geometry {
	private ObjectList<OrderedTriple> points;
	private List<Listener> listeners = new ArrayList<Listener>();

	public Geometry(ObjectList<OrderedTriple> points) {
		this.points = new ObjectList<OrderedTriple>(points.size(), 10);
		for (OrderedTriple p : points) {
			this.points.add(p);
		}
	}

	public OrderedTriple getPoint(int index) {
		return points.get(index);
	}

	public int getPointCount() {
		return points.size();
	}

	public ObjectList<OrderedTriple> getPoints() {
		return points;
	}

	public void addListener(Listener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void notifyListeners() {
		for (Listener listener : listeners) {
			listener.geometryChanged();
		}
	}

	public double getMaxRadius() {
		double cur, max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < points.num; ++i) {
			cur = getPoint(i).lengthSquared();
			if (cur > max)
				max = cur;
		}
		return Math.sqrt(max);
	}

	public int registerPoint(OrderedTriple p) throws Exception {// adds point if
		for (int i = 0; i < points.num; ++i) {
			OrderedTriple curp = (OrderedTriple) points.get(i);
			if (p.distanceSquared(curp) < ArchiBuilder.REGISTER_POINT_EPSILON) {
				return i;
			}
		}
		return points.addReturnIndex(p);
	}

	public interface Listener {
		public void geometryChanged();
	}
}
