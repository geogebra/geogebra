// Copyright 2007, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * @author Steffen Greiffenberg
 * @version $Id: AbstractPolygon.java,v 1.4 2009-06-22 02:18:17 hohenwarter Exp
 *          $
 */
public abstract class AbstractPolygon extends EMFTag {

	private Rectangle bounds;

	private int numberOfPoints;

	private Point[] points;

	protected AbstractPolygon(int id, int version) {
		super(id, version);
	}

	protected AbstractPolygon(int id, int version, Rectangle bounds,
			int numberOfPoints, Point[] points) {
		super(id, version);
		this.bounds = bounds;
		this.numberOfPoints = numberOfPoints;
		this.points = points;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeRECTL(bounds);
		emf.writeDWORD(numberOfPoints);
		emf.writePOINTL(numberOfPoints, points);
	}

	@Override
	public String toString() {
		String result = super.toString() + "\n  bounds: " + bounds
				+ "\n  #points: " + numberOfPoints;
		if (points != null) {
			result += "\n  points: ";
			for (int i = 0; i < points.length; i++) {
				result += "[" + points[i].x + "," + points[i].y + "]";
				if (i < points.length - 1) {
					result += ", ";
				}
			}
		}
		return result;
	}

	protected Rectangle getBounds() {
		return bounds;
	}

	protected int getNumberOfPoints() {
		return numberOfPoints;
	}

	protected Point[] getPoints() {
		return points;
	}
}
