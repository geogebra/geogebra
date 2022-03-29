package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.SegmentStyle;

public class DrawSegmentStyle {
	private GeoElement geo;
	private GLine2D line;
	private DrawSegment drawSegment;

	public DrawSegmentStyle(DrawSegment drawSegment) {
		this.drawSegment = drawSegment;
	}

	public void draw(GGraphics2D g2, SegmentStyle style, boolean isStartStyle) {
		if (style == SegmentStyle.DEFAULT) {
			return;
		}
		line = drawSegment.getLine();
		geo = drawSegment.getGeoElement();

		int lineThickness = geo.getLineThickness();
		int posX = isStartStyle ? (int) line.getX1() - lineThickness
				: (int) line.getX2() - lineThickness;
		int posY = isStartStyle ? (int) line.getY1() - lineThickness
				: (int) line.getY2() - lineThickness;

		double deltaX = line.getX2() - line.getX1();
		double deltaY = line.getY2() - line.getY1();
		double angle = Math.atan2(deltaY, deltaX);
		GAffineTransform t = AwtFactory.getPrototype().newAffineTransform();

		if (style == SegmentStyle.CIRCLE_OUTLINE || style == SegmentStyle.SQUARE_OUTLINE) {
			g2.setColor(GColor.WHITE);
		}

		switch (style) {
		case DEFAULT:
			break;
		case LINE:
			drawLine(g2, isStartStyle, lineThickness, angle, t);
			break;
		case SQUARE_OUTLINE:
		case SQUARE:
			drawSquare(g2, lineThickness, posX, posY, angle, t);
			break;
		case CIRCLE:
		case CIRCLE_OUTLINE:
			drawCircle(g2, lineThickness, posX, posY);
			break;
		case ARROW:
		case ARROW_FILLED:
			drawArrow(g2, style, isStartStyle, lineThickness, angle, t);
			break;
		}
	}

	private void drawLine(GGraphics2D g2, boolean isStartStyle, int lineThickness, double angle,
			GAffineTransform t) {
		double x1 = isStartStyle ? line.getX1() : line.getX2();
		double y1 = isStartStyle ? line.getY1() - lineThickness
				: line.getY2() - lineThickness;
		double y2 = isStartStyle ? line.getY1() + lineThickness
				: line.getY2() + lineThickness;

		initRotateTrans(angle, x1, y1 + lineThickness, t);
		GLine2D line2D = AwtFactory.getPrototype().newLine2D();
		line2D.setLine(x1, y1, x1, y2);
		GShape rotatedLine = t.createTransformedShape(line2D);
		g2.draw(rotatedLine);
	}

	private void drawArrow(GGraphics2D g2, SegmentStyle style, boolean isStartStyle,
			int lineThickness, double angle, GAffineTransform t) {
		double x = isStartStyle ? line.getX1() : line.getX2();
		double y = isStartStyle ? line.getY1() : line.getY2();
		double arrowSideX = isStartStyle ? x + lineThickness : x - lineThickness;

		initRotateTrans(angle, x, y, t);
		GGeneralPath arrowPath = AwtFactory.getPrototype().newGeneralPath();
		arrowPath.moveTo(x, y);

		arrowPath.lineTo(arrowSideX, y + lineThickness);
		if (style == SegmentStyle.ARROW_FILLED) {
			arrowPath.lineTo(arrowSideX, y - lineThickness);
			arrowPath.closePath();
		} else {
			arrowPath.moveTo(arrowSideX, y - lineThickness);
			arrowPath.lineTo(x, y);
		}
		GShape rotatedArrow = t.createTransformedShape(arrowPath);

		g2.setColor(geo.getObjectColor());
		if (style == SegmentStyle.ARROW_FILLED) {
			g2.fill(rotatedArrow);
		}
		g2.draw(rotatedArrow);
	}

	private void drawCircle(GGraphics2D g2, int lineThickness, int posX, int posY) {
		GEllipse2DDouble circleOutline = AwtFactory.getPrototype().newEllipse2DDouble();
		circleOutline.setFrame(posX, posY, lineThickness * 2, lineThickness * 2);
		g2.fill(circleOutline);
		g2.setColor(geo.getObjectColor());
		g2.draw(circleOutline);
	}

	private void drawSquare(GGraphics2D g2, int lineThickness, int posX, int posY, double angle,
			GAffineTransform t) {
		initRotateTrans(angle, posX + lineThickness,
				posY + lineThickness, t);
		GRectangle2D r = AwtFactory.getPrototype().newRectangle(posX, posY,
				lineThickness * 2, lineThickness * 2);
		GShape rotatedSquare = t.createTransformedShape(r);
		g2.fill(rotatedSquare);
		g2.setColor(geo.getObjectColor());
		g2.draw(rotatedSquare);
	}

	private void initRotateTrans(double angle, double transX, double transY,
			GAffineTransform trans) {
		trans.translate(transX, transY);
		trans.rotate(angle);
		trans.translate(-transX, -transY);
	}
}
