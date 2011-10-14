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

package com.quantimegroup.solutions.archimedean.gui;import java.awt.Color;import java.awt.Event;import java.awt.Graphics;import java.awt.Image;import java.awt.event.MouseEvent;import javax.swing.JComponent;import javax.swing.event.ChangeEvent;import javax.swing.event.ChangeListener;import javax.swing.event.MouseInputListener;public class RainbowSlider extends JComponent implements MouseInputListener {	private Image img, img2;	private int minval, maxval, val = 0;	public RainbowSlider() {		addMouseListener(this);		addMouseMotionListener(this);	}	public void paintComponent(Graphics g) {		if (img == null){			img = createImage(getWidth(), getHeight());			Graphics g1 = img.getGraphics();			g1.setColor(getBackground());			g1.fillRect(0, 0, getWidth(), getHeight());			for (int i = 0; i < maxval; ++i){				g1.setColor(getColor(i));				g1.drawLine(i + 8, 3, i + 8, getHeight() - 5);			}			g1.setColor(Color.black);			g1.drawRect(7, 3, maxval, getHeight() - 7);			img2 = createImage(getWidth(), getHeight());		}		Graphics g2 = img2.getGraphics();		g2.drawImage(img, 0, 0, this);		g2.setColor(getColor(val));		g2.fillRect(val, 0, 15, getHeight() - 1);		g2.setColor(Color.black);		g2.drawRect(val, 0, 15, getHeight() - 1);		g.drawImage(img2, 0, 0, this);	}	void MouseDrag(Event e) {		MouseDown(e);	}	void MouseDown(Event e) {		setVal(e.x - 8);	}	public void setVal(int value) {		val = value;		if (val < minval)			val = minval;		else if (val >= maxval)			val = maxval - 1;		repaint();		ChangeListener[] listeners = (ChangeListener[]) this.getListeners(ChangeListener.class);		for (int i = 0; i < listeners.length; ++i){			listeners[i].stateChanged(new ChangeEvent(this));		}	}	Color getColor(int i) {		return Color.getHSBColor((float) i / maxval, 1f, 1f);	}	public Color getColor() {		return getColor(val);	}	float getHue(int i) {		return (float) i / maxval;	}	public float getHue() {		return getHue(val);	}	public synchronized void reshape(int x, int y, int width, int height) {		super.reshape(x, y, width, height);		minval = 0;		maxval = width - 16;	}	public void mouseClicked(MouseEvent e) {	}	public void mouseEntered(MouseEvent e) {	}	public void mouseExited(MouseEvent e) {	}	public void mousePressed(MouseEvent e) {		setVal(e.getX() - 8);	}	public void mouseReleased(MouseEvent e) {	}	public void mouseDragged(MouseEvent e) {		mousePressed(e);	}	public void mouseMoved(MouseEvent e) {	}	public void addChangeListener(ChangeListener cl) {		listenerList.add(ChangeListener.class, cl);	}	public void removeChangeListener(ChangeListener cl) {		listenerList.remove(ChangeListener.class, cl);	}	public int getMaxval() {		return maxval;	}	public void setMaxval(int maxval) {		this.maxval = maxval;	}}