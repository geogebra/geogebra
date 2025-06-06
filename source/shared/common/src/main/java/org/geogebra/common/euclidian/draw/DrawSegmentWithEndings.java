package org.geogebra.common.euclidian.draw;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.HasSegmentStyle;
import org.geogebra.common.kernel.geos.SegmentStyle;

public class DrawSegmentWithEndings {
	private final HasSegmentStyle segment;
	//private GLine2D line;
	//private final Drawable drawSegment;
	private final EndDecoratedDrawable drawable;
	private int lineThickness;
	private int posX;
	private int posY;
	private boolean isStartStyle;
	private @CheckForNull GShape solidStart;
	private @CheckForNull GShape solidEnd;
	private GShape subtractedLine;

	/**
	 * @param drawable segment drawable
	 * @param segment segment construction element
	 */
	public DrawSegmentWithEndings(EndDecoratedDrawable drawable,
			HasSegmentStyle segment) {
		this.segment = segment;
		this.drawable = drawable;
	}

	/**
	 * Update all parts of the drawn shape
	 */
	public void update(GBasicStroke objStroke) {
		SegmentStyle startStyle = segment.getStartStyle();
		SegmentStyle endStyle = segment.getEndStyle();

		createSolidShape(objStroke, startStyle, endStyle);
	}

	/**
	 * Draws the segment with endings (if any)
	 * @param g2 {@link GGraphics2D}
	 */
	public void draw(GGraphics2D g2) {
		if (drawable.isHighlighted()) {
			drawable.setHighlightingStyle(g2);
			g2.draw(drawable.getLine());
			drawEndingIfExists(solidStart, g2, true);
			drawEndingIfExists(solidEnd, g2, true);
		}
		drawable.setBasicStyle(g2);
		g2.fill(subtractedLine);

		drawEndingIfExists(solidStart, g2, segment.getStartStyle().isOutline());
		drawEndingIfExists(solidEnd, g2, segment.getEndStyle().isOutline());
	}

	private void drawEndingIfExists(GShape end, GGraphics2D g2, boolean outline) {
		if (end != null) {
			if (outline) {
				g2.draw(end);
			} else {
				g2.fill(end);
			}
		}
	}

	private void createSolidShape(GBasicStroke objStroke,
			SegmentStyle startStyle, SegmentStyle endStyle) {
		solidStart = createSolidStart(startStyle);
		solidEnd = createSolidEnd(endStyle);
		subtractedLine = subtractFromLine(objStroke, solidStart, solidEnd);
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

	private GArea subtractFromLine(GBasicStroke objStroke, GShape... shapes) {
		GShape strokedLine = objStroke.createStrokedShape(drawable.getLine(),
				255);
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
		case ARROW_OUTLINE:
		case ARROW_FILLED:
			return getArrow(style);
		case DIAMOND:
		case DIAMOND_OUTLINE:
			return getSolidDiamond();
		case CROWS_FOOT:
			return getCrowsFoot();
		}
		return null;
	}

	private GShape getLine() {
		double x1 = isStartStyle ? getX1() : getX2();
		double y1 = isStartStyle ? getY1() - lineThickness
				: getY2() - lineThickness;
		double y2 = isStartStyle ? getY1() + lineThickness
				: getY2() + lineThickness;

		GAffineTransform t = AwtFactory.getPrototype().newAffineTransform();
		initRotateTrans(getAngle(), x1, y1 + lineThickness, t);
		GLine2D line2D = AwtFactory.getPrototype().newLine2D();
		line2D.setLine(x1, y1, x1, y2);
		GShape strokedShape = drawable.getDecoStroke().createStrokedShape(line2D, 255);
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

	private GShape getCrowsFoot() {
		double x = isStartStyle ? getX1() : getX2();
		double y = isStartStyle ? getY1() : getY2();
		double dist = isStartStyle ? x - lineThickness : x + lineThickness;

		GAffineTransform t = AwtFactory.getPrototype().newAffineTransform();
		initRotateTrans(getAngle(), x, y, t);
		GGeneralPath crowsFootPath = AwtFactory.getPrototype().newGeneralPath();
		crowsFootPath.moveTo(x, y);
		crowsFootPath.lineTo(dist, y + lineThickness);
		crowsFootPath.moveTo(x, y);
		crowsFootPath.lineTo(dist, y);
		crowsFootPath.moveTo(x, y);
		crowsFootPath.lineTo(dist, y - lineThickness);

		GShape strokedCrowsFoot = createStrokedShape(crowsFootPath);
		return t.createTransformedShape(strokedCrowsFoot);
	}

	private GShape getSolidDiamond() {
		double x = isStartStyle ? getX1() : getX2();
		double y = isStartStyle ? getY1() : getY2();

		GAffineTransform t = AwtFactory.getPrototype().newAffineTransform();
		initRotateTrans(getAngle(), x, y, t);
		GGeneralPath diamondPath = AwtFactory.getPrototype().newGeneralPath();
		diamondPath.moveTo(x + lineThickness, y);
		diamondPath.lineTo(x, y - lineThickness);
		diamondPath.lineTo(x - lineThickness, y);
		diamondPath.lineTo(x, y + lineThickness);
		diamondPath.closePath();

		GShape strokedDiamond = AwtFactory.getPrototype().newMyBasicStroke(0.5f)
				.createStrokedShape(diamondPath, 255);
		GShape transformedDiamond = t.createTransformedShape(strokedDiamond);
		GArea area = GCompositeShape.toArea(transformedDiamond);
		area.add(GCompositeShape.toArea(t.createTransformedShape(diamondPath)));
		return area;
	}

	private GShape createStrokedShape(GShape shape) {
		return drawable.getDecoStroke().createStrokedShape(shape, 255);
	}

	private void calculatePositions() {
		lineThickness = segment.getLineThickness();
		posX = isStartStyle ? (int) getX1() - lineThickness
				: (int) getX2() - lineThickness;
		posY = isStartStyle ? (int) getY1() - lineThickness
				: (int) getY2() - lineThickness;
	}

	private double getX1() {
		return drawable.getX1();
	}

	private double getX2() {
		return drawable.getX2();
	}

	private double getY1() {
		return drawable.getY1();
	}

	private double getY2() {
		return drawable.getY2();
	}

	private double getAngle() {
		return drawable.getAngle(isStartStyle);
	}

	private GShape getArrow(SegmentStyle style) {
		double x = isStartStyle ? getX1() : getX2();
		double y = isStartStyle ? getY1() : getY2();
		double arrowSideX = isStartStyle ? x + lineThickness : x - lineThickness;

		GAffineTransform t = AwtFactory.getPrototype().newAffineTransform();
		initRotateTrans(getAngle(), x, y, t);
		GGeneralPath arrowPath = AwtFactory.getPrototype().newGeneralPath();
		arrowPath.moveTo(x, y);

		arrowPath.lineTo(arrowSideX, y + lineThickness);
		boolean filled = style.equals(SegmentStyle.ARROW_FILLED);
		boolean outlined = style.equals(SegmentStyle.ARROW_OUTLINE);
		if (filled || outlined) {
			arrowPath.lineTo(arrowSideX, y - lineThickness);
			arrowPath.closePath();
		} else {
			arrowPath.moveTo(arrowSideX, y - lineThickness);
			arrowPath.lineTo(x, y);
		}

		GShape strokedArrow = outlined ? AwtFactory.getPrototype().newBasicStroke(0.5f)
				.createStrokedShape(arrowPath, 255) : createStrokedShape(arrowPath);
		GShape transformedArrow = t.createTransformedShape(strokedArrow);
		if (filled || outlined) {
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
