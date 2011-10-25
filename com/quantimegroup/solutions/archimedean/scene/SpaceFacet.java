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
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.List;

import com.quantimegroup.solutions.archimedean.geom.GeometryUtils;
import com.quantimegroup.solutions.archimedean.utils.IntList;
import com.quantimegroup.solutions.archimedean.utils.OrderedTriple;
import com.quantimegroup.solutions.archimedean.utils.SmartPolygon;

public class SpaceFacet {
	private OrderedTriple normal = new OrderedTriple();
	private List<SpacePoint> points;
	private IntList pointIndices;
	private Polygon polygon = new Polygon();

	public SpaceFacet(List<SpacePoint> points, int[] pointIndices) {
		this.points = points;
		this.pointIndices = new IntList(pointIndices);
	}

	public OrderedTriple getNormal() {
		return normal;
	}

	public boolean visible() {
		return SpacePoint.viewer.minus(getPoint(0)).dot(getNormal()) >= 0;
	}

	public OrderedTriple getPoint(int i) {
		return points.get(pointIndices.get(i));
	}

	public boolean inside(int x, int y) {
		return SmartPolygon.inside(polygon, x, y);
	}

	public OrderedTriple sectLine(OrderedTriple L1, OrderedTriple L2) {
		OrderedTriple[] p = threeDistinctPoints();
		return OrderedTriple.sectPlaneLine(p[0], p[1], p[2], L1, L2);
	}

	public OrderedTriple[] threeDistinctPoints() {
		// assumes that there are three distinct points
		OrderedTriple[] p = new OrderedTriple[3];
		double epsilon = 1e-10;

		p[0] = getPoint(0);
		int i;
		for (i = 1; i < pointIndices.size(); ++i) {
			p[1] = getPoint(i);
			if (!p[1].isApprox(p[0], epsilon))
				break;
		}
		for (i = i + 1; i < pointIndices.size(); ++i) {
			p[2] = getPoint(i);
			if (!p[2].isApprox(p[1], epsilon))
				break;
		}
		return p;
	}

	public void update() {
		polygon.reset();
		for (int i = 0; i < pointIndices.size(); ++i) {
			SpacePoint sp = points.get(pointIndices.get(i));
			polygon.addPoint(sp.screenx, sp.screeny);
		}
		polygon.addPoint(polygon.xpoints[0], polygon.ypoints[0]);

	}

	public void render(Graphics g, IScenePrefs scenePrefs) {
		if (scenePrefs.isDraw()) {
			draw(g, scenePrefs, false);
		} else {
			fill(g, scenePrefs);
			if (scenePrefs.isDrawEdges()) {
				draw(g, scenePrefs, false);
			}
		}
	}

	private static void printPolygon(Polygon p) {
		System.out.println("x" + new IntList(p.xpoints, p.npoints));
		System.out.println("y" + new IntList(p.ypoints, p.npoints));
	}

	public void draw(Graphics g, IScenePrefs scenePrefs, boolean transparent) {
		// if (!transparent && !visible())
		// return;
		if (scenePrefs.isDraw()) {
			if (!visible()) {
				g.setColor(Color.getHSBColor(scenePrefs.getHue1(), 1f, 0.4f));
			} else {
				g.setColor(Color.getHSBColor(scenePrefs.getHue1(), 1f, 1f));
			}
		} else {
			// g.setColor(getEdgeColor());
			g.setColor(Color.BLACK);
		}
		g.drawPolygon(polygon);
	}

	public void draw(Graphics g, IScenePrefs scenePrefs) {
		draw(g, scenePrefs, false);
	}

	public void fill(Graphics g, IScenePrefs scenePrefs) {
		if (!visible())
			return;
		OrderedTriple ray = scenePrefs.getLightSource().minus(getPoint(0));
		double brightness = (ray.dot(getNormal())) / (getNormal().length() * ray.length());
		if (brightness < 0)
			brightness *= -0.5;
		else
			brightness *= 0.8;
		brightness += 0.2;

		float hue = scenePrefs.getHue(this);

		double white = 1;
		if (visible()) {// add reflected light
			// OrderedTriple p0 = getPoint( 0 );
			try {
				OrderedTriple p0 = getCenter();
				OrderedTriple p1 = scenePrefs.getLightSource();
				OrderedTriple v1 = p1.minus(p0);
				OrderedTriple N = getNormal().unit();
				double theta = N.radBetween(v1);
				N.timesEquals(N.comp(v1));
				OrderedTriple p3 = p0.plus(N);
				OrderedTriple p2 = p3.times(2).minus(p1);
				OrderedTriple v2 = p2.minus(p0);
				OrderedTriple S = SpacePoint.viewer.minus(p0);
				double alpha = S.radBetween(v2);
				double maxAngle = Math.PI / 2;
				if (alpha < maxAngle) {
					white = Math.pow(alpha / maxAngle, .25);
					white -= .2;
					white = Math.max(white, 0);
				}
			} catch (Exception e) {
				// center didn't work probably because the side is infinitesimal
			}
		}

		g.setColor(Color.getHSBColor(hue, (float) white, (float) brightness));
		g.fillPolygon(polygon);
	}

	public OrderedTriple getCenter() {// works only for sides that can be
		// inscribed in circles
		OrderedTriple[] p = threeDistinctPoints();
		return GeometryUtils.getCircumcenter(p[0], p[1], p[2]);
	}

	public int getPointIndex(int i) {
		return pointIndices.get(i);
	}

	public IntList getPointIndices() {
		return pointIndices;
	}

	public int getVertexCount() {
		return pointIndices.size();
	}

	public String toString() {
		int count = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("\t" + count++ + "" + getPointIndices() + "\n");
		sb.append("\tnormal: " + getNormal() + "\n");
		return sb.toString();

	}
}
