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

package com.quantimegroup.solutions.archimedean.scene;

import com.quantimegroup.solutions.archimedean.utils.IntList;
import com.quantimegroup.solutions.archimedean.utils.ObjectList;
import com.quantimegroup.solutions.archimedean.utils.OrderedTriple;

public class Facet {
	private final Surface surface;
	private IntList pointIndices = new IntList();
	private ObjectList<OrderedTriple> points = new ObjectList<OrderedTriple>();
	private OrderedTriple normal;

	public Facet(Surface surface, int[] pointIndices, OrderedTriple normal) {
		this.surface = surface;
		this.pointIndices = new IntList(pointIndices);
		points = new ObjectList<OrderedTriple>(pointIndices.length);
		for (int index : pointIndices) {
			points.add(surface.getPoint(index));
		}
		this.normal = normal;
	}

	public IntList getPointIndices() {
		return pointIndices;
	}

	public int getPointIndex(int i) {
		return pointIndices.get(i);
	}

	public ObjectList<OrderedTriple> getPoints() {
		return points;
	}

	public OrderedTriple getNormal() {
		return normal;
	}

	public String toString() {
		return String.valueOf(pointIndices);
	}

}
