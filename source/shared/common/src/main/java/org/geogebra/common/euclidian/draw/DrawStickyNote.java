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

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MediaBoundingBox;
import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.kernel.geos.GeoInline;
import org.jspecify.annotations.Nullable;

/**
 * Class that handles drawing sticky note elements.
 */
public class DrawStickyNote extends Drawable implements DrawInline {
	public static final int PADDING = 12;

	protected final GeoInline stickyNote;
	protected @Nullable InlineTextController textController;

	protected final TransformableRectangle rectangle;

	/**
	 * Create a new DrawInlineText instance.
	 *
	 * @param view view
	 * @param stickyNote geo element
	 */
	public DrawStickyNote(EuclidianView view, GeoInline stickyNote) {
		super(view, stickyNote);
		rectangle = new TransformableRectangle(view, stickyNote, true);
		this.stickyNote = stickyNote;
		this.textController = view.getApplication().createInlineTextController(view, stickyNote);
		createEditor();
	}

	private void createEditor() {
		if (textController != null) {
			textController.create();
		}
	}

	@Override
	public void update() {
		stickyNote.zoomIfNeeded();
		rectangle.updateSelfAndBoundingBox();

		GPoint2D point = stickyNote.getLocation();
		if (textController != null && point != null) {
			double contentWidth = stickyNote.getContentWidth();
			double contentHeight = stickyNote.getContentHeight();
			double angle = stickyNote.getAngle();
			double width = stickyNote.getWidth();
			double height = stickyNote.getHeight();

			textController.setLocation(
					view.toScreenCoordX(point.getX()), view.toScreenCoordY(point.getY()));
			textController.setHeight((int) contentHeight - 2 * PADDING);
			textController.setWidth((int) contentWidth - 2 * PADDING);
			textController.setTransform(angle, width / contentWidth, height / contentHeight);
		}
	}

	@Override
	public void updateContent() {
		if (textController != null) {
			textController.updateContentIfChanged();
		}
	}

	@Override
	public boolean hasContent() {
		// no controller == loaded from file, assume not empty
		return textController == null || textController.hasContent();
	}

	@Override
	public void saveContent() {
		if (textController != null) {
			textController.saveContent();
		}
	}

	@Override
	public double getScale() {
		return rectangle.realWidth() / stickyNote.getContentWidth();
	}

	@Override
	public GAffineTransform getTransform() {
		return rectangle.getDirectTransform();
	}

	@Override
	public void toBackground(SuspensionTrigger trigger) {
		if (textController != null) {
			textController.toBackground(trigger);
		}
	}

	@Override
	public void toForeground(int x, int y) {
		if (textController != null) {
			GPoint2D p = rectangle.getInversePoint(x - PADDING, y - PADDING);
			textController.toForeground((int) p.getX(), (int) p.getY());
		}
	}

	@Override
	public String urlByCoordinate(int x, int y) {
		if (textController != null) {
			GPoint2D p = rectangle.getInversePoint(x - PADDING, y - PADDING);
			return textController.urlByCoordinate((int) p.getX(), (int) p.getY());
		}

		return "";
	}

	@Override
	public GRectangle getBounds() {
		return rectangle.getBounds();
	}

	@Override
	public MediaBoundingBox getBoundingBox() {
		return rectangle.getBoundingBox();
	}

	@Override
	public void draw(GGraphics2D g2) {
		draw(g2, 0);
	}

	@Override
	public boolean isInteractiveEditor() {
		return textController != null && textController.isEditing();
	}

	protected void draw(GGraphics2D g2, int borderRadius) {
		if (stickyNote.isEuclidianVisible()
				&& textController != null
				&& rectangle.getDirectTransform() != null) {
			double contentWidth = stickyNote.getContentWidth();
			double contentHeight = stickyNote.getContentHeight();
			GAffineTransform tr = rectangle.scaleForZoom(contentWidth, contentHeight);
			g2.saveTransform();
			g2.transform(tr);

			if (geo.getBackgroundColor() != null) {
				g2.setPaint(geo.getBackgroundColor());
				g2.setShadowProperties("rgba(0, 0, 0, 0.12)", 0, 8, 12);
				g2.fillRoundRect(
						0, 0, (int) contentWidth, (int) contentHeight, 2 * borderRadius, 2 * borderRadius);
				g2.setShadowProperties("rgba(0, 0, 0, 0)", 0, 8, 0);
			}

			textController.draw(g2);

			g2.restoreTransform();
		}
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return rectangle.hit(x, y);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(getBounds());
	}

	@Override
	public void remove() {
		if (textController != null) {
			textController.discard();
		}
	}

	@Override
	public void updateByBoundingBoxResize(GPoint2D point, EuclidianBoundingBoxHandler handler) {
		rectangle.updateByBoundingBoxResize(point, handler);
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> points) {
		rectangle.fromPoints(points);
	}

	@Override
	protected List<GPoint2D> toPoints() {
		return rectangle.toPoints();
	}

	@Override
	public BoundingBox<? extends GShape> getSelectionBoundingBox() {
		return getBoundingBox();
	}

	@Override
	public InlineTextController getController() {
		return textController;
	}

	/**
	 * Setter to mock Carota.
	 * Nicer solutions are welcome.
	 *
	 * @param textController to set.
	 */
	public void setTextController(InlineTextController textController) {
		this.textController = textController;
	}
}
