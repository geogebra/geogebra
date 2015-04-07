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

package org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.geom;

import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.ObjectList;
import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.OrderedTriple;

public class GeometryUtils {
	public static ObjectList<OrderedTriple> createPoly(int numEdges, double edgeLength) {
		ObjectList<OrderedTriple> points = new ObjectList<OrderedTriple>(numEdges);
		double dtheta = Math.PI * 2 / numEdges;
		double theta = dtheta / 2 - Math.PI / 2;
		double radius = edgeLength / (2 * Math.sin(dtheta / 2));
		for (int i = 0; i < numEdges; ++i, theta -= dtheta) {
			OrderedTriple p = new OrderedTriple(Math.cos(theta), 0, Math.sin(theta));
			p.timesEquals(radius);
			points.add(p);
		}
		return points;
	}

	public static OrderedTriple getCircumcenter(OrderedTriple p1, OrderedTriple p2, OrderedTriple p3) {
		OrderedTriple e1 = p2.minus(p1);
		OrderedTriple m1 = p2.mid(p1);

		OrderedTriple e2 = p3.minus(p2);
		OrderedTriple m2 = p3.mid(p2);

		OrderedTriple normal = e1.cross(e2);

		OrderedTriple r1 = normal.cross(e1);
		OrderedTriple r2 = normal.cross(e2);
		return OrderedTriple.sectLines(m1, m1.plus(r1), m2, m2.plus(r2));
	}

	public static OrderedTriple getIncenter(OrderedTriple p1, OrderedTriple p2, OrderedTriple p3, OrderedTriple p4) {
		OrderedTriple v1 = p2.minus(p1).unit().times(100);
		OrderedTriple v2 = p3.minus(p2).unit().times(100);
		OrderedTriple v3 = p4.minus(p3).unit().times(100);
		OrderedTriple mid1 = v1.negative().mid(v2);
		OrderedTriple mid2 = v2.negative().mid(v3);

		return OrderedTriple.sectLines(p2, mid1.plus(p2), p3, mid2.plus(p3));
	}

	public static ObjectList<OrderedTriple> circumscribedTangent(ObjectList<OrderedTriple> s) {
		OrderedTriple p1 = (OrderedTriple) s.get(0), p2 = (OrderedTriple) s.get(1), p3 = (OrderedTriple) s.get(2);
		OrderedTriple normal = p2.minus(p1).cross(p3.minus(p2));

		OrderedTriple center = GeometryUtils.getCircumcenter(p1, p2, p3);// get its
		// center
		ObjectList<OrderedTriple> tangents = new ObjectList<OrderedTriple>(s.num);
		for (int j = 0; j < s.num; ++j) {// construct tangents to its circumscribed
			// circle at its points
			OrderedTriple radiusVector = ((OrderedTriple) s.get(j)).minus(center);
			OrderedTriple tangent = radiusVector.cross(normal);
			tangents.add(tangent);
		}
		ObjectList<OrderedTriple> tangentSide = new ObjectList<OrderedTriple>(s.num);
		for (int j = 0; j < tangents.num; ++j) {// intersect consecutive tangents
			// and add point to dualSide
			p1 = (OrderedTriple) s.get(j);
			p2 = (OrderedTriple) s.wrapget(j + 1);
			OrderedTriple tanv1 = (OrderedTriple) tangents.get(j);
			OrderedTriple tanv2 = (OrderedTriple) tangents.wrapget(j + 1);
			OrderedTriple intersection = OrderedTriple.sectLines(p1, p1.plus(tanv1), p2, p2.plus(tanv2));
			tangentSide.add(intersection);
		}
		return tangentSide;
	}

	static public double triangleArea(double a, double b, double c) {
		double s = (a + b + c) / 2;
		return Math.sqrt(s * (s - a) * (s - b) * (s - c));
	}

	public static OrderedTriple[] threeDistinctPoints(IFacet facet) {
		// assumes that there are three distinct points
		OrderedTriple[] p = new OrderedTriple[3];
		double epsilon = 1e-10;

		p[0] = facet.getPoint(0);
		int i;
		for (i = 1; i < facet.getVertexCount(); ++i) {
			p[1] = facet.getPoint(i);
			if (!p[1].isApprox(p[0], epsilon))
				break;
		}
		for (i = i + 1; i < facet.getVertexCount(); ++i) {
			p[2] = facet.getPoint(i);
			if (!p[2].isApprox(p[1], epsilon))
				break;
		}
		return p;
	}
	
	
	public static OrderedTriple sectLine(IFacet facet, OrderedTriple L1, OrderedTriple L2) {
		OrderedTriple[] p = GeometryUtils.threeDistinctPoints(facet);
		return OrderedTriple.sectPlaneLine(p[0], p[1], p[2], L1, L2);
	}

	
	public static OrderedTriple getCenter(IFacet facet) {// works only for sides that can be
		// inscribed in circles
		OrderedTriple[] p = threeDistinctPoints(facet);
		return GeometryUtils.getCircumcenter(p[0], p[1], p[2]);
	}
	
}
