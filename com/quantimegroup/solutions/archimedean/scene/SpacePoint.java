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

package com.quantimegroup.solutions.archimedean.scene;import com.quantimegroup.solutions.archimedean.utils.OrderedDouble;import com.quantimegroup.solutions.archimedean.utils.OrderedTriple;public class SpacePoint extends OrderedTriple {	public int screenx, screeny;	public static int originx, originy;	public static OrderedTriple viewer;	private static double VIEWER_DISTANCE = -2400;	public SpacePoint() {		super();	}	public SpacePoint(double x, double y, double z) {		super(x, y, z);	}	public SpacePoint(OrderedTriple t) {		super(t.x, t.y, t.z);	}	public SpacePoint(int X, int Y) {		this(X - originx, 0, originy - Y);	}	public static void init(int w, int h) {		viewer = new OrderedTriple(0, VIEWER_DISTANCE, 0);		originx = w / 2;		originy = h / 2;	}	public SpacePoint toScreenCoord() {		//Converts a 3D coordinate to its 2D screen mapping		double K = (double) viewer.y / (viewer.y - y);		screenx = (int) (Math.round(K * x)) + originx;		screeny = -(int) (Math.round(K * z)) + originy;		return this;	}	public void print() {		super.print();		System.out.println(screenx + ", " + screeny);	}	public OrderedDouble getScreenPoint() {		return new OrderedDouble(screenx, screeny);	}}