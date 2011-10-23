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

package com.quantimegroup.solutions.archimedean.scene;public class TruncableSpaceSide extends SpaceSide {	private boolean cornerSide;	private int[] neighbors = new int[4];	public TruncableSpaceSide(int numPoints) {		super(numPoints);	}	public int theoreticalNumPoints() {		throw new UnsupportedOperationException();	}	public boolean isCornerSide() {		return cornerSide;	}	public void setCornerSide(boolean cornerSide) {		this.cornerSide = cornerSide;	}	public void setNeighbors(int... neighbors) {		for (int i = 0; i < this.neighbors.length; ++i) {			this.neighbors[i] = neighbors[i];		}	}	public int[] getNeighbors() {		return neighbors;	}}