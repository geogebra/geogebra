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

package com.quantimegroup.solutions.archimedean.gui;import java.awt.Color;import java.awt.Graphics;import javax.swing.ButtonModel;import javax.swing.JButton;public class FramedButton extends JButton {	private FrameBorder border;	public FramedButton() {		border = new FrameBorder(getBackground(), 4);		border.setInverted(true);		setBorder(border);		setContentAreaFilled(false);	}	public FramedButton(String text) {		this();		setText(text);	}	public void paintBorder(Graphics g) {		ButtonModel bm = getModel();		if (bm.isPressed() || !bm.isEnabled()){			border.setInverted(false);		}else{			border.setInverted(true);		}		super.paintBorder(g);	}	public void paintComponent(Graphics g) {		ButtonModel bm = getModel();		int w = this.getWidth();		int h = this.getHeight();		Color cacheColor = g.getColor();		g.setColor(getBackground());		g.fillRect(0, 0, w, h);		g.setColor(cacheColor);		if (bm.isEnabled()){			super.paintComponent(g);		}	}	public void setFrameThickness(int i) {		border.setThickness(i);	}	public int getFrameThickness() {		return border.getThickness();	}}