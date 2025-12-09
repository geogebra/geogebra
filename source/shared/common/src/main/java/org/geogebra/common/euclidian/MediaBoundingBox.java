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

package org.geogebra.common.euclidian;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.RectangleTransformable;

public class MediaBoundingBox extends BoundingBox<GShape> {

	protected RectangleTransformable geo;
	protected GAffineTransform transform;
	protected GPoint2D[] corners = new GPoint2D[9];
	GLine2D line = AwtFactory.getPrototype().newLine2D();
	protected GColor secondaryColor;
	BoundingBoxDelegate delegate;

	/**
	 * Inline bounding box (which rotates with element)
	 * @param rotationImage - rotation icon
	 */
	public MediaBoundingBox(MyImage rotationImage) {
		delegate = new RotatableBoundingBox(this);
		setRotationHandlerImage(rotationImage);
	}

	public void setSecondaryColor(GColor secondaryColor) {
		this.secondaryColor = secondaryColor;
	}

	@Override
	protected void createHandlers() {
		delegate.createHandlers();
	}

	@Override
	protected GShape createCornerHandler() {
		return delegate.createCornerHandler();
	}

	@Override
	protected GShape createSideHandler() {
		return delegate.createSideHandler();
	}

	@Override
	public void draw(GGraphics2D g2) {
		g2.setStroke(AwtFactory.getPrototype().newBasicStroke(2.0f, GBasicStroke.CAP_BUTT,
				GBasicStroke.JOIN_MITER));
		g2.setColor(color);
		if (corners[0] != null) {
			for (int i = 0; i < 4; i++) {
				line.setLine(corners[i].getX(), corners[i].getY(),
						corners[(i + 1) % 4].getX(), corners[(i + 1) % 4].getY());
				g2.draw(line);
			}
		}
		if (showHandlers()) {
			delegate.draw(g2);
		}
	}

	@Override
	protected void drawHandlers(GGraphics2D g2) {
		for (GShape handler : handlers) {
			if (isSideHandler(handler)) {
				drawRotatedSideHandler(g2, (GRectangle2D) handler);
			} else {
				fillHandlerWhite(g2, handler);
				setHandlerBorderStyle(g2);
				g2.draw(handler);
			}
		}

		if (handlers.size() > ROTATION_HANDLER_INDEX) {
			drawRotationHandler(g2);
		}
	}

	private void drawRotatedSideHandler(GGraphics2D g2, GRectangle2D sideHandler) {
		double centerHandlerX = sideHandler.getX() + sideHandler.getWidth() / 2;
		double centerHandlerY = sideHandler.getY() + sideHandler.getHeight() / 2;
		double angle = getAngle();

		GAffineTransform transform = createRotateTransformation(centerHandlerX, centerHandlerY,
				angle);
		drawTransformedHandler(g2, transform, sideHandler);
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		return delegate.hitSideOfBoundingBox(x, y, hitThreshold);
	}

	@Override
	public void setTransform(GAffineTransform directTransform) {
		this.transform = directTransform;
		updateHandlers();
	}

	protected void setHandlerTransformed(int handlerIndex, double x, double y) {
		corners[handlerIndex] = transform.transform(new GPoint2D(x, y), null);
		delegate.setHandlerFromCenter(handlerIndex,
				corners[handlerIndex].getX(), corners[handlerIndex].getY());
	}

	protected void updateHandlers() {
		double width = geo.getWidth();
		double height = geo.getHeight();

		setHandlerTransformed(0, 0, 0);
		setHandlerTransformed(1, 0, height);
		setHandlerTransformed(2, width, height);
		setHandlerTransformed(3, width, 0);
		setHandlerTransformed(4, width / 2, 0);
		setHandlerTransformed(5, 0, height / 2);
		setHandlerTransformed(6, width / 2, height);
		setHandlerTransformed(7, width, height / 2);
		if (handlers.size() > 8) {
			setHandlerTransformed(8, width / 2,
					height + BoundingBox.ROTATION_HANDLER_DISTANCE);
		}
	}

	/**
	 * @param crop true for crop mode, false for resize
	 */
	public void setCropMode(boolean crop) {
		if (crop != isCropBox()) {
			delegate = crop ? new CropBox(this) : new RotatableBoundingBox(this);
			delegate.createHandlers();
			updateHandlers();
		}
	}

	@Override
	public boolean isCropBox() {
		return delegate instanceof CropBox;
	}

	@Override
	public void updateFrom(GeoElement geo) {
		super.updateFrom(geo);
		this.geo = (RectangleTransformable) geo;
	}

	protected boolean showHandlers() {
		return !((GeoElement) geo).hasGroup();
	}

	@Override
	public EuclidianCursor getCursor(EuclidianBoundingBoxHandler handler) {
		if (handler == EuclidianBoundingBoxHandler.ROTATION) {
			return EuclidianCursor.ROTATION;
		}

		// evil hackery to get closest rotation handler
		int cursorIndex = (int) Math.round(4 * (Math.atan2(handler.getDx(), handler.getDy())
				- geo.getAngle()) / Math.PI) % 4;

		// I'd need a proper number theoretic remainder, but I have to make do with
		// Computer Science modulo (there is Math.floorMod in java8)
		switch ((4 + cursorIndex) % 4) {
		case 0:
			return EuclidianCursor.RESIZE_NS;
		case 1:
			return EuclidianCursor.RESIZE_NWSE;
		case 2:
			return EuclidianCursor.RESIZE_EW;
		case 3:
			return EuclidianCursor.RESIZE_NESW;
		default:
			return null; // never happens
		}
	}

	private double getAngle() {
		GRectangle2D rightTop = handlers.get(2).getBounds2D();
		GRectangle2D leftTop = handlers.get(1).getBounds2D();
		double deltaX = rightTop.getX() + rightTop.getWidth() / 2
				- (leftTop.getX() + leftTop.getWidth() / 2);
		double deltaY = rightTop.getY() + rightTop.getWidth() / 2
				- (leftTop.getY() + leftTop.getWidth() / 2);
		return Math.atan2(deltaY, deltaX);
	}

	private GAffineTransform createRotateTransformation(double centerX, double centerY,
			double angle) {
		GAffineTransform t = AwtFactory.getPrototype().newAffineTransform();
		t.translate(centerX, centerY);
		t.rotate(angle);
		t.translate(-centerX, -centerY);
		return t;
	}

	private void drawTransformedHandler(GGraphics2D g2, GAffineTransform transform,
			GRectangle2D sideHandler) {
		g2.saveTransform();
		g2.transform(transform);
		drawSideHandler(g2, sideHandler);
		g2.restoreTransform();
	}

	private void drawSideHandler(GGraphics2D g2, GRectangle2D sideHandler) {
		fillHandlerWhite(g2, sideHandler);
		setHandlerBorderStyle(g2);
		drawRoundedRectangle(g2, sideHandler);
	}
}
