	package org.geogebra.common.euclidian.draw;

	import org.geogebra.common.awt.GAffineTransform;
	import org.geogebra.common.awt.GBasicStroke;
	import org.geogebra.common.awt.GGeneralPath;
	import org.geogebra.common.awt.GLine2D;
	import org.geogebra.common.awt.GShape;
	import org.geogebra.common.factories.AwtFactory;

public class RotatedArrow {
	private final GLine2D line;
	private final double lineThickness;
	private final GBasicStroke stroke;
	private final GGeneralPath arrow;

	/**
	 *
	 * @param line vector line to attach arrow.
	 * @param lineThickness the thickness of the line.
	 * @param stroke to make the arrow of.
	 */
	RotatedArrow(GLine2D line, double lineThickness, GBasicStroke stroke) {
		this.line = line;
		this.lineThickness = lineThickness;
		this.stroke = stroke;
		arrow = AwtFactory.getPrototype().newGeneralPath();
	}

	private double getAngle() {
		double deltaX = line.getX2() - line.getX1();
		double deltaY = line.getY2() - line.getY1();
		return Math.atan2(deltaY, deltaX);
	}

	/**
	 *
	 * @return the result shape.
	 */
	GShape get() {
		double x = line.getX2();
		double y = line.getY2();
		double arrowSideX = x - lineThickness;

		GAffineTransform t = AwtFactory.getPrototype().newAffineTransform();
		initRotateTrans(getAngle(), x, y, t);

		arrow.reset();
		arrow.moveTo(x, y);
		arrow.lineTo(arrowSideX, y + lineThickness);
		arrow.moveTo(arrowSideX, y - lineThickness);
		arrow.lineTo(x, y);

		GShape strokedArrow = stroke.createStrokedShape(arrow, 255);
		return t.createTransformedShape(strokedArrow);
	}

	private void initRotateTrans(double angle, double transX, double transY,
			GAffineTransform trans) {
		trans.translate(transX, transY);
		trans.rotate(angle);
		trans.translate(-transX, -transY);
	}
}
