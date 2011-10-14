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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Iterator;

public class NullLayout implements LayoutManager {
	private HashSet components = new HashSet();

	public void removeLayoutComponent(Component comp) {
		components.remove(comp);
	}

	public void layoutContainer(Container parent) {
		parent.setSize(preferredLayoutSize(parent));
	}

	public void addLayoutComponent(String name, Component comp) {
		components.add(comp);
	}

	public Dimension minimumLayoutSize(Container parent) {
		Component[] components = parent.getComponents();
		if (components.length == 0){
			return new Dimension(0, 0);
		}else{
			int minX = Integer.MAX_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int maxY = Integer.MIN_VALUE;
			Rectangle r = new Rectangle();
			for (int i = 0; i < components.length; ++i){
				Component comp = components[i];
				comp.getBounds(r);
				minX = Math.min(minX, r.x);
				minY = Math.min(minY, r.y);
				maxX = Math.max(minX, r.x + r.width);
				maxY = Math.max(minY, r.y + r.height);
			}
			return new Dimension(maxX - minX, maxY - minY);
		}
	}

	public Dimension preferredLayoutSize(Container parent) {
		return minimumLayoutSize(parent);
	}

}
