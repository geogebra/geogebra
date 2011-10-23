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

import java.util.ArrayList;
import java.util.List;

import com.quantimegroup.solutions.archimedean.utils.OrderedTriple;



public class Surface extends Geometry {
	private List<Facet> facets = new ArrayList<Facet>();
	private List<Listener> listeners = new ArrayList<Listener>();

	public Surface(OrderedTriple[] points) {
		super(points);
	}

	public void addFacet(Facet f) {
		facets.add(f);
	}

	public Facet getFacet(int index) {
		return facets.get(index);
	}

	public List<Facet> getFacets() {
		return facets;
	}

	public int getFacetCount() {
		return facets.size();
	}

	public void addListener(Listener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void notifyListeners() {
		for (Listener listener : listeners) {
			listener.surfaceChanged();
		}
	}

	static interface Listener {
		public void surfaceChanged();
	}
}
