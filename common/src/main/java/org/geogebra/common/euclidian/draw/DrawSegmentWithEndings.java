package org.geogebra.common.euclidian.draw;

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
	private GLine2D line;
	private DrawSegment drawSegment;
	private int lineThickness;
	private int posX;
	private int posY;
	private boolean isStartStyle;
	private GShape shape;

	public DrawSegmentWithEndings(DrawSegment drawSegment) {
		this.drawSegment = drawSegment;
	}

	/**
	 * Draws the segment with endings (if any)
	 * @param g2 {@link GGraphics2D}
	 */
	public void draw(GGraphics2D g2) {
		GeoElement geo = drawSegment.getGeoElement();
		if (!geo.isGeoSegment()) {
			return;
		}

		GeoSegment segment = (GeoSegment) geo;
		SegmentStyle startStyle = segment.getStartStyle();
		SegmentStyle endStyle = segment.getEndStyle();
		shape = startStyle.isOutline() || endStyle.isOutline()
				? createOutlinedShape(startStyle, endStyle)
			    : createSolidShape(startStyle, endStyle);
		draw(g2, shape);
	}

	private GShape createOutlinedShape(SegmentStyle startStyle, SegmentStyle endStyle) {
		GShape solidStart = createSolidStart(startStyle);
		GShape solidEnd = createSolidEnd(endStyle);
		GArea subtractedLine = substractFromLine(solidStart, solidEnd);
		GShape outlinedStart = createOutlinedStart(startStyle);
		GShape outlinedEnd = createOutlinedEnd(endStyle);
		GShape lineWithEnds = union(subtractedLine,
				outlinedStart == null ? solidStart : outlinedStart,
				outlinedEnd == null ? solidEnd : outlinedEnd);
		return lineWithEnds;
	}

	private GShape createOutlinedStart(SegmentStyle style) {
		isStartStyle = true;
		calculatePositions();
		return createOutlinedEnding(style);
	}

	private GShape createOutlinedEnd(SegmentStyle style) {
		isStartStyle = false;
		calculatePositions();
		return createOutlinedEnding(style);
	}

	private GShape createSolidShape(SegmentStyle startStyle, SegmentStyle endStyle) {
		GShape solidStart = createSolidStart(startStyle);
		GShape solidEnd = createSolidEnd(endStyle);
		GArea subtractedLine = substractFromLine(solidStart, solidEnd);
		GShape lineWithEnds = union(subtractedLine, solidStart, solidEnd);
		return lineWithEnds;
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

	private void draw(GGraphics2D g2, GShape lineWithEnds) {
		if (drawSegment.isHighlighted()) {
			drawSegment.drawHighlighted(g2, lineWithEnds);
		}
		drawSegment.fillShape(g2, lineWithEnds);
	}

	private GShape union(GArea composite, GShape... shapes) {
		for (GShape shape : shapes) {
			if (shape != null) {
				composite.add(toArea(shape));
			}
		}
		return composite;
	}

	private GArea substractFromLine(GShape... shapes) {
		GShape strokedLine = drawSegment.getObjStroke().createStrokedShape(line, 255);
		GArea area = toArea(strokedLine);
		for (GShape shape : shapes) {
			if (shape != null) {
				area.subtract(toArea(shape));
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
		GShape rotatedLine = t.createTransformedShape(strokedShape);
		return rotatedLine;
	}

	private GArea toArea(GShape shape) {
		return AwtFactory.getPrototype().newArea(shape);
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
		GShape rotatedSquare = t.createTransformedShape(r);
		return rotatedSquare;
	}

	private GShape createOutlinedEnding(SegmentStyle style) {
		if (style.isDefault() || !style.isOutline()) {
			return null;
		}
		GAffineTransform t = AwtFactory.getPrototype().newAffineTransform();
		initRotateTrans(getAngle(), posX + lineThickness,
				posY + lineThickness, t);
		switch (style) {
		case SQUARE:
		case SQUARE_OUTLINE:
			return getOutlinedSquare(t);
		case CIRCLE:
		case CIRCLE_OUTLINE:
			return getOutlinedCircle();

		}
		return null;
	}

	private GShape getOutlinedCircle() {
		GEllipse2DDouble circleOutline = AwtFactory.getPrototype().newEllipse2DDouble();
		circleOutline.setFrame(posX, posY, lineThickness * 2, lineThickness * 2);
		return createStrokedShape(circleOutline);
	}

	private GShape createStrokedShape(GShape shape) {
		return drawSegment.getDecoStroke().createStrokedShape(shape, 255);
	}

	private GShape getOutlinedSquare(GAffineTransform t) {
		GRectangle2D r = AwtFactory.getPrototype().newRectangle(posX, posY,
				lineThickness * 2, lineThickness * 2);
		GShape squareOutline = createStrokedShape(r);
		GShape rotatedSquare = t.createTransformedShape(squareOutline);
		return rotatedSquare;
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
		double angle = Math.atan2(deltaY, deltaX);
		return angle;
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
			GArea area = toArea(transformedArrow);
			area.add(toArea(t.createTransformedShape(arrowPath)));
			return area;
		}
		return transformedArrow;
	}

	public GShape getShape() {
		return shape;
	}
}
