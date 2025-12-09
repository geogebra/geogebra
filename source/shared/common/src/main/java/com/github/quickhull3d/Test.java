/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package com.github.quickhull3d;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// x y z coordinates of 6 points
		Point3d[] points = new Point3d[] { new Point3d(0.0, 0.0, 0.0),
				new Point3d(1.0, 0.5, 0.0), new Point3d(2.0, 0.0, 0.0),
				new Point3d(0.5, 0.5, 0.5), new Point3d(0.0, 0.0, 2.0),
				new Point3d(0.1, 0.2, 0.3), new Point3d(0.0, 2.0, 0.0), };

		QuickHull3D hull = new QuickHull3D();
		hull.build(points);

		System.out.println("Vertices:");
		Point3d[] vertices = hull.getVertices();
		for (int i = 0; i < vertices.length; i++) {
			Point3d pnt = vertices[i];
			System.out.println(pnt.x + " " + pnt.y + " " + pnt.z);
		}

		System.out.println("Faces:");
		int[][] faceIndices = hull.getFaces(QuickHull3D.POINT_RELATIVE);
		for (int i = 0; i < vertices.length; i++) {
			for (int k = 0; k < faceIndices[i].length; k++) {
				System.out.print(faceIndices[i][k] + " ");
			}
			System.out.println("");
		}
	}

}
