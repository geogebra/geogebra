/*
 * Copyright 2008 the original author or authors.
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package geogebra.common.kernel.discrete.alds.ds.graphs;

import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Represents a Vertex in a graph.
 * 
 * @author Devender
 * 
 */
public final class Vertex {

	private final String name;
	private GeoPointND point;

	public Vertex(String name, GeoPointND point) {
		this.name = name;
		this.point = point;
	}

	public String getName() {
		return name;
	}
	
	public GeoPointND getPoint() {
		return point;
	}

	public String toString() {
		return name;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Vertex)) {
			return false;
		}
		Vertex other = (Vertex) obj;

		return (this.name.equals(other.name));
	}

	public int hashCode() {
		int result = 19;
		if (name != null) {
			result = 31 + name.hashCode();
		}
		return result;
	}

}