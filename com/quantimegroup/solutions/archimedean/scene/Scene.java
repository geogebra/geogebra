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
import java.awt.Graphics;

import com.quantimegroup.solutions.archimedean.utils.OrderedTriple;

public class Scene {
	private SpacePoly poly;
	private IScenePrefs prefs = createScenePrefs();

	public Scene() {

	}

	public SpacePoly getPoly() {
		return poly;
	}

	public void setPoly(SpacePoly poly) {
		this.poly = poly;
	}

	public IScenePrefs getScenePrefs() {
		return prefs;
	}

	public void setScenePrefs(IScenePrefs prefs) {
		this.prefs = prefs;
	}

	public void render(Graphics g) {
		if (poly != null) {
			poly.render(g, prefs);
		}
	}

	public void update() {
		if (poly != null) {
			poly.update();
		}
	}

	private static IScenePrefs createScenePrefs() {
		IScenePrefs sp = new IScenePrefs() {
			private boolean draw = false;
			private boolean drawEdges = true;
			float hue1 = 0.5f;
			float hue2 = 0.5f;
			Color color1;
			Color color2;
			Color edgeColor = Color.BLACK;
			OrderedTriple lightSource = new OrderedTriple(20000, -100000, 20000);
			boolean colorCoding = true;

			public boolean isColorCoding() {
				return colorCoding;
			}

			public Dimension getCanvasSize() {
				// TODO Auto-generated method stub
				return null;
			}

			public void setColorCoding(boolean colorCoding) {
				this.colorCoding = colorCoding;
			}
			
			public Color getColor(int polyType) {
				if (colorCoding)
					return Color.getHSBColor(intToHue(polyType), 1f, 1f);
				else
					return color1;
			}


			private float intToHue(int i) {
				float[] hues = new float[9];
				hues[3] = Color.RGBtoHSB(Color.red.getRed(), Color.red.getGreen(), Color.red.getBlue(), null)[0];
				hues[4] = Color.RGBtoHSB(Color.yellow.getRed(), Color.yellow.getGreen(), Color.yellow.getBlue(), null)[0];
				hues[5] = Color.RGBtoHSB(Color.blue.getRed(), Color.blue.getGreen(), Color.blue.getBlue(), null)[0];
				hues[6] = Color.RGBtoHSB(Color.green.getRed(), Color.green.getGreen(), Color.green.getBlue(), null)[0];
				hues[7] = Color.RGBtoHSB(Color.cyan.getRed(), Color.cyan.getGreen(), Color.cyan.getBlue(), null)[0];
				hues[8] = Color.RGBtoHSB(Color.magenta.getRed(), Color.magenta.getGreen(), Color.magenta.getBlue(), null)[0];
				if (i > 8)
					i = 7;
				return hues[i];
			}

			public float getHue(SpaceFacet s) {
				if (colorCoding) {
					int p = s.getVertexCount();
					return intToHue(p);
				} else {
					return hue1;
				}
			}

			public float getHue1() {
				return hue1;
			}

			public float getHue2() {
				return hue2;
			}

			public void setHue2(float hue2) {
				this.hue2 = hue2;
			}

			public boolean isDraw() {
				return draw;
			}

			public boolean isDrawEdges() {
				return drawEdges;
			}

			public void repaint() {
				// TODO Auto-generated method stub

			}

			public void setDraw(boolean draw) {
				this.draw = draw;
			}

			public void setDrawEdges(boolean drawEdges) {
				this.drawEdges = drawEdges;

			}

			public void setHue1(float hue1) {
				this.hue1 = hue1;
			}

			public OrderedTriple getLightSource() {
				return lightSource;
			}

			public Color getColor1() {
				// TODO Auto-generated method stub
				return color1;
			}

			public Color getColor2() {
				// TODO Auto-generated method stub
				return color2;
			}

			public void setColor1(Color color) {
				color1 = color;
			}

			public void setColor2(Color color) {
				color2 = color;
			}

			public Color getEdgeColor() {
				return edgeColor;
			}

			public void setEdgeColor(Color color) {
				edgeColor = color;

			}

		};
		return sp;
	}

}
