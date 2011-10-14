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
import java.awt.GridBagLayout;
import java.awt.LayoutManager2;

import javax.swing.JComponent;

public class GBBoxLayout implements LayoutManager2 {
	private GridBagLayout delegate = new GridBagLayout();
	private int axis;
	private int mode;

	public final static int X_AXIS = 1;
	public final static int Y_AXIS = 2;
	public final static int PERCENT_MODE = 1;
	public final static int ACTUAL_MODE = 2;

	public GBBoxLayout(int axis, int mode) {
		this.axis = axis;
		this.mode = mode;
	}

	public float getLayoutAlignmentX(Container target) {
		return delegate.getLayoutAlignmentX(target);
	}

	public float getLayoutAlignmentY(Container target) {
		return delegate.getLayoutAlignmentY(target);
	}

	public void invalidateLayout(Container target) {
		delegate.invalidateLayout(target);
	}

	public Dimension maximumLayoutSize(Container target) {
		return delegate.maximumLayoutSize(target);
	}

	public void addLayoutComponent(Component comp, Object constraints) {
		delegate.addLayoutComponent(comp, constraints);
	}

	public void removeLayoutComponent(Component comp) {
		delegate.removeLayoutComponent(comp);
	}

	public void layoutContainer(Container parent) {
		delegate.layoutContainer(parent);
	}

	public void addLayoutComponent(String name, Component comp) {
		delegate.addLayoutComponent(name, comp);
	}

	public Dimension minimumLayoutSize(Container parent) {
		return delegate.minimumLayoutSize(parent);
	}

	public Dimension preferredLayoutSize(Container parent) {
		return delegate.preferredLayoutSize(parent);
	}

	public void addSpace(int size) {

	}

	private static class Space extends JComponent {
		int size;
	}

}
