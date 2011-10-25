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

import java.awt.Color;
import java.awt.Dimension;

import com.quantimegroup.solutions.archimedean.utils.OrderedTriple;

public interface IScenePrefs {

	public Dimension getCanvasSize();

	public boolean isDraw();

	public void setDraw(boolean draw);

	public float getHue1();

	public float getHue2();

	public float getHue(SpaceFacet ss);

	public void setHue1(float hue1);

	public void setHue2(float hue2);

	public Color getColor(int polyType);

	public Color getColor1();

	public void setColor1(Color color);

	public Color getColor2();

	public void setColor2(Color color);

	public boolean isDrawEdges();

	public void setDrawEdges(boolean drawEdges);

	public void repaint();

	public OrderedTriple getLightSource();

	public void setColorCoding(boolean selected);

	public boolean isColorCoding();

	public Color getEdgeColor();

	public void setEdgeColor(Color color);
	
	public void setPrimaryColoring(boolean b);
}
