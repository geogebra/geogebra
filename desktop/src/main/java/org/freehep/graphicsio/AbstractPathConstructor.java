// Copyright 2001 freehep
package org.freehep.graphicsio;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.io.IOException;

/**
 * Implements some of the PathConstructor functionality
 * 
 * @author Mark Donszelmann
 * @version $Id: AbstractPathConstructor.java,v 1.4 2009-08-17 21:44:45 murkle
 *          Exp $
 */
public abstract class AbstractPathConstructor implements PathConstructor {

	protected double currentX, currentY;

	protected AbstractPathConstructor() {
		currentX = 0;
		currentY = 0;
	}

	@Override
	public void flush() throws IOException {
		currentX = 0;
		currentY = 0;
	}

	@Override
	public boolean addPath(Shape s) throws IOException {
		return addPath(s, null);
	}

	@Override
	public boolean addPath(Shape s, AffineTransform transform)
			throws IOException {
		return addPath(this, s, transform);
	}

	public static boolean addPath(PathConstructor out, Shape s,
			AffineTransform transform) throws IOException {
		PathIterator path = s.getPathIterator(transform);
		double[] coords = new double[6];
		double pathStartX = 0.;
		double pathStartY = 0.;
		while (!path.isDone()) {
			int segType = path.currentSegment(coords);

			switch (segType) {
			case PathIterator.SEG_MOVETO:
				out.move(coords[0], coords[1]);
				pathStartX = coords[0];
				pathStartY = coords[1];
				break;
			case PathIterator.SEG_LINETO:
				out.line(coords[0], coords[1]);
				break;
			case PathIterator.SEG_QUADTO:
				out.quad(coords[0], coords[1], coords[2], coords[3]);
				break;
			case PathIterator.SEG_CUBICTO:
				out.cubic(coords[0], coords[1], coords[2], coords[3], coords[4],
						coords[5]);
				break;
			case PathIterator.SEG_CLOSE:
				out.closePath(pathStartX, pathStartY);
				break;
			}
			// Move to the next segment.
			path.next();
		}
		out.flush();
		return (path.getWindingRule() == PathIterator.WIND_EVEN_ODD);
	}

	public static boolean isEvenOdd(Shape s) {
		return s.getPathIterator(null)
				.getWindingRule() == PathIterator.WIND_EVEN_ODD;
	}
}
