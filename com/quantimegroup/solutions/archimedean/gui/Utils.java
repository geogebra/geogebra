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

import java.awt.Dimension;

import javax.swing.JComponent;

public class Utils {

	public static void freezeSize(JComponent comp, int width, int height) {
		Dimension d = new Dimension(width, height);
		comp.setPreferredSize(d);
		comp.setMaximumSize(d);
		comp.setMinimumSize(d);
	}

	public static void freezeHeight(JComponent comp, int height) {
		comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, height));
		comp.setMaximumSize(new Dimension(comp.getMaximumSize().width, height));
		comp.setMinimumSize(new Dimension(comp.getMinimumSize().width, height));
	}
}
