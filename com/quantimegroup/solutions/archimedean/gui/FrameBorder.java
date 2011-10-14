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

package com.quantimegroup.solutions.archimedean.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;

import javax.swing.JButton;
import javax.swing.border.Border;

public class FrameBorder implements Border {
	private Insets insets;
	private Color color;
	private boolean inverted = false;

	public FrameBorder(Color color, int thickness) {
		insets = new Insets(thickness, thickness, thickness, thickness);
		this.color = color;
	}

	public boolean isBorderOpaque() {
		return false;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Polygon darkPoly = new Polygon();
		darkPoly.npoints = 7;
		darkPoly.xpoints = new int[] { width + x, x, x, x + insets.left, x + insets.left, width + x - insets.right, width + x };
		darkPoly.ypoints = new int[] { y, y, height + y, height + y - insets.bottom, y + insets.top, y + insets.top, y };

		Polygon brightPoly = new Polygon();
		brightPoly.npoints = 7;
		brightPoly.xpoints = new int[] { x, width + x, width + x, width + x - insets.right, width + x - insets.right, x + insets.left, x };
		brightPoly.ypoints = new int[] { height + y, height + y, y, y + insets.top, height + y - insets.bottom, height + y - insets.bottom,
				height + y };
		Color colorOld = g.getColor();
		Color dark = color.darker();
		Color bright = color.brighter();
		if (inverted){
			Color temp = dark;
			dark = bright;
			bright = temp;
		}

		g.setColor(dark);
		g.fillPolygon(darkPoly);
		g.setColor(bright);
		g.fillPolygon(brightPoly);
		g.setColor(colorOld);
	}

	public Insets getBorderInsets(Component c) {
		return insets;
	}

	public void setInverted(boolean invert) {
		this.inverted = invert;
	}

	public boolean isInverted() {
		return inverted;
	}
	public void setThickness(int thickness){
		insets.left = insets.right = insets.top = insets.bottom = thickness;
	}
	public int getThickness(){
		return insets.left;
	}

}
