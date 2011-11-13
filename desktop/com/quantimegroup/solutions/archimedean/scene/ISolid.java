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

import com.quantimegroup.solutions.archimedean.utils.OrderedTriple;

public interface ISolid {
	/**
	 * Get the number of vertices in this IArchimedeanSolid
	 * 
	 * @return
	 */
	public int getVertexCount();

	/**
	 * Return all the vertices of this IArchimedeanSolid.
	 * 
	 * @return an array of vertices
	 */
	public OrderedTriple[] getVertices();

	/**
	 * Return all the normals of this IArchimedeanSolid.
	 * 
	 * @return an array of normals
	 */
	public OrderedTriple[] getNormals();

	/**
	 * Get the number of faces in this IArchimedeanSolid
	 * 
	 * @return
	 */
	public int getSideCount();

	/**
	 * Return all the faces of this IArchimedeanSolid.
	 * 
	 * @return an array of faces
	 */
	public ISide[] getSides();

	public ISide createFace();

}
