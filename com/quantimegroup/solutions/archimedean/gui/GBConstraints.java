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

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GBConstraints extends GridBagConstraints {
	public int minWidth;
	public int minHeight;

	public void setSize(int gridwidth, int gridheight) {
		this.gridwidth = gridwidth;
		this.gridheight = gridheight;
	}

	public void setGrid(int gridx, int gridy) {
		this.gridx = gridx;
		this.gridy = gridy;
	}

	public void setWeight(double weightx, double weighty) {
		this.weightx = weightx;
		this.weighty = weighty;
	}

	public void setInsets(int top, int left, int bottom, int right) {
		insets = new Insets(top, left, bottom, right);
	}

	public void setFill(int fill, int anchor) {
		this.fill = fill;
		this.anchor = anchor;
	}

	public void setIPad(int ipadx, int ipady) {
		this.ipadx = ipadx;
		this.ipady = ipady;
	}

	public void setMinSize(int minWidth, int minHeight) {
		this.minWidth = minWidth;
		this.minHeight = minHeight;
	}

	public void clear() {
		setSize(1, 1);
		setGrid(RELATIVE, RELATIVE);
		setWeight(0, 0);
		setInsets(0, 0, 0, 0);
		setFill(NONE, CENTER);
		setIPad(0, 0);
	}
}
