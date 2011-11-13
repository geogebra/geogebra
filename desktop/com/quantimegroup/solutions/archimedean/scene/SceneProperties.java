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

import java.awt.Dimension;

public class SceneProperties {
	private Dimension size;
	private boolean draw;
	private float hue1;
	private boolean drawEdges;

	public Dimension getSize() {
		return size;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	public boolean isDraw() {
		return draw;
	}

	public void setDraw(boolean draw) {
		this.draw = draw;
	}

	public float getHue1() {
		return hue1;
	}

	public void setHue1(float hue1) {
		this.hue1 = hue1;
	}

	public boolean isDrawEdges() {
		return drawEdges;
	}

	public void setDrawEdges(boolean drawEdges) {
		this.drawEdges = drawEdges;
	}

}
