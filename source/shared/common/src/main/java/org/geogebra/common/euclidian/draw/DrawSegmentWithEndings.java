package org.geogebra.common.euclidian.draw;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.SegmentStyle;

public class DrawSegmentWithEndings {
	private final GeoSegment segment;
	private GLine2D line;
	private final DrawSegment drawSegment;
	private int lineThickness;
	private int posX;
	private int posY;
	private boolean isStartStyle;
	private @CheckForNull GShape solidStart;
	private @CheckForNull GShape solidEnd;
	private GShape subtractedLine;

	/**
	 * @param drawSegment segment drawable
	 * @param segment segment construction element
	 */
	public DrawSegmentWithEndings(DrawSegment drawSegment, GeoSegment segment) {
		this.drawSegment = drawSegment;
		this.segment = segment;
	}

	/**
	 * Update all parts of the drawn shape
	 */
	public void update() {
		SegmentStyle startStyle = segment.getStartStyle();
		SegmentStyle endStyle = segment.getEndStyle();

		createSolidShape(startStyle, endStyle);
	}

	/**
	 * Draws the segment with endings (if any)
	 * @param g2 {@link GGraphics2D}
	 */
	public void draw(GGraphics2D g2) {
		if (drawSegment.isHighlighted()) {
			drawSegment.setHighlightingStyle(g2);
			g2.draw(line);
			drawSafely(solidStart, g2, true);
			drawSafely(solidEnd, g2, true);
		}
		drawSegment.setBasicStyle(g2);
		g2.fill(subtractedLine);

		drawSafely(solidStart, g2, segment.getStartStyle().isOutline());
		drawSafely(solidEnd, g2, segment.getEndStyle().isOutline());
	}

	private void drawSafely(GShape end, GGraphics2D g2, boolean outline) {
		if (end != null) {
			if (outline) {
				g2.draw(end);
			} else {
				g2.fill(end);
			}
		}
	}

	private void createSolidShape(SegmentStyle startStyle, SegmentStyle endStyle) {
		solidStart = createSolidStart(startStyle);
		solidEnd = createSolidEnd(endStyle);
		subtractedLine = subtractFromLine(solidStart, solidEnd);
	}

	private GShape createSolidStart(SegmentStyle style) {
		isStartStyle = true;
		calculatePositions();
		return createSolidEnding(style);
	}

	private GShape createSolidEnd(SegmentStyle style) {
		isStartStyle = false;
		calculatePositions();
		return createSolidEnding(style);
	}

	private GArea subtractFromLine(GShape... shapes) {
		GShape strokedLine = drawSegment.getObjStroke().createStrokedShape(line, 255);
		GArea area = GCompositeShape.toArea(strokedLine);
		for (GShape shape : shapes) {
			if (shape != null) {
				area.subtract(GCompositeShape.toArea(shape));
			}
		}
		return area;
	}

	private GShape createSolidEnding(SegmentStyle style) {
		if (style.isDefault()) {
			return null;
		}
		GAffineTransform t = AwtFactory.getPrototype().newAffineTransform();
		initRotateTrans(getAngle(), posX + lineThickness,
				posY + lineThickness, t);
		switch (style) {
		case LINE:
			return getLine();
		case SQUARE:
		case SQUARE_OUTLINE:
			return getSolidSquare(t);
		case CIRCLE:
		case CIRCLE_OUTLINE:
			return getSolidCircle();
		case ARROW:
		case ARROW_FILLED:
			return getArrow(style);
		}
		return null;
	}

	private GShape getLine() {
		double x1 = isStartStyle ? line.getX1() : line.getX2();
		double y1 = isStartStyle ? line.getY1() - lineThickness
				: line.getY2() - lineThickness;
		double y2 = isStartStyle ? line.getY1() + lineThickness
				: line.getY2() + lineThickness;

		GAffineTransform t = AwtFactory.getPrototype().newAffineTransform();
		initRotateTrans(getAngle(), x1, y1 + lineThickness, t);
		GLine2D line2D = AwtFactory.getPrototype().newLine2D();
		line2D.setLine(x1, y1, x1, y2);
		GShape strokedShape = drawSegment.getDecoStroke().createStrokedShape(line2D, 255);
		return t.createTransformedShape(strokedShape);
	}

	private void initRotateTrans(double angle, double transX, double transY,
			GAffineTransform trans) {
		trans.translate(transX, transY);
		trans.rotate(angle);
		trans.translate(-transX, -transY);
	}

	private GShape getSolidCircle() {
		GEllipse2DDouble circleOutline = AwtFactory.getPrototype().newEllipse2DDouble();
		circleOutline.setFrame(posX, posY, lineThickness * 2, lineThickness * 2);
		return circleOutline;
	}

	private GShape getSolidSquare(GAffineTransform t) {
		GRectangle2D r = AwtFactory.getPrototype().newRectangle(posX, posY,
				lineThickness * 2, lineThickness * 2);
		return t.createTransformedShape(r);
	}

	private GShape createStrokedShape(GShape shape) {
		return drawSegment.getDecoStroke().createStrokedShape(shape, 255);
	}

	private void calculatePositions() {
		GeoElement geo = drawSegment.getGeoElement();
		line = drawSegment.getLine();
		lineThickness = geo.getLineThickness();
		posX = isStartStyle ? (int) line.getX1() - lineThickness
				: (int) line.getX2() - lineThickness;
		posY = isStartStyle ? (int) line.getY1() - lineThickness
				: (int) line.getY2() - lineThickness;
	}

	private double getAngle() {
		double deltaX = line.getX2() - line.getX1();
		double deltaY = line.getY2() - line.getY1();
		return Math.atan2(deltaY, deltaX);
	}

	private GShape getArrow(SegmentStyle style) {
		double x = isStartStyle ? line.getX1() : line.getX2();
		double y = isStartStyle ? line.getY1() : line.getY2();
		double arrowSideX = isStartStyle ? x + lineThickness : x - lineThickness;

		GAffineTransform t = AwtFactory.getPrototype().newAffineTransform();
		initRotateTrans(getAngle(), x, y, t);
		GGeneralPath arrowPath = AwtFactory.getPrototype().newGeneralPath();
		arrowPath.moveTo(x, y);

		arrowPath.lineTo(arrowSideX, y + lineThickness);
		boolean filled = style.equals(SegmentStyle.ARROW_FILLED);
		if (filled) {
			arrowPath.lineTo(arrowSideX, y - lineThickness);
			arrowPath.closePath();
		} else {
			arrowPath.moveTo(arrowSideX, y - lineThickness);
			arrowPath.lineTo(x, y);
		}
		GShape strokedArrow = createStrokedShape(arrowPath);
		GShape transformedArrow = t.createTransformedShape(strokedArrow);
		if (filled) {
			GArea area = GCompositeShape.toArea(transformedArrow);
			area.add(GCompositeShape.toArea(t.createTransformedShape(arrowPath)));
			return area;
		}
		return transformedArrow;
	}

	public GShape getShape() {
		return subtractedLine;
	}
}
