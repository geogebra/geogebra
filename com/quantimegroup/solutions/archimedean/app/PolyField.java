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

package com.quantimegroup.solutions.archimedean.app;import java.awt.*;class PolyField extends TextField {	protected Archimedean gui;	public void setGUI(Archimedean g) {		gui = g;	}	public boolean checkTheta(int numEntries, int polyType) {		if (polyType < 3) return false;		if (gui.manualMode) return true;		double theta = Math.PI * (1.0 - 2.0 / polyType) + gui.sumTheta;		if (numEntries >= 3) return theta < 2 * Math.PI ? true : false;		if (numEntries == 2) return theta < 2 * Math.PI - Math.PI / 3 ? true : false;		return true;	}	public boolean keyDown(Event evt, int key) {		String s;		if (key == '\b'){			if (getText().length() <= 1) s = "";			else s = new String(getText().toCharArray(), 0, getText().length() - 1);		}else{			s = getText() + (char) key;		}		boolean accept = false;		try{			int x = Integer.parseInt(s);			if (x == 0) accept = false;			else if (x == 1 || x == 2) accept = checkTheta(gui.numEntries + 1, x * 10);			else accept = checkTheta(gui.numEntries + 1, x);		}catch (Exception e){			if (key == '\b') return false;			return true;		}		return !accept;	}	boolean thetaGood() {		try{			int x = Integer.parseInt(getText());			return x >= 3;		}catch (Exception e){			return false;		}	}	public boolean handleEvent(Event event) {		return super.handleEvent(event);	}}