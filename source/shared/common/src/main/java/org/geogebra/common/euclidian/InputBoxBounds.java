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
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.draw.TextRenderer;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.util.MyMath;

public class InputBoxBounds {
	private GRectangle bounds;
	private final GeoInputBox geoInputBox;
	private TextRenderer renderer;
	private TextRendererSettings settings;

	/**
	 *
	 * @param geoInputBox which bounds belongs of.
	 */
	public InputBoxBounds(GeoInputBox geoInputBox) {
		this.geoInputBox = geoInputBox;
		bounds = AwtFactory.getPrototype().newRectangle();
	}

	/**
	 *
	 * @return the bounds.
	 */
	public GRectangle getBounds() {
		return bounds;
	}

	/**
	 *
	 * @param view {@link EuclidianView}
	 * @param labelTop top of the label.
	 * @param textFont font to display.
	 * @param labelDesc label description.
	 */
	public void update(EuclidianView view, double labelTop, GFont textFont, String labelDesc) {
		GGraphics2D g2 = view.getTempGraphics2D(textFont);
		bounds = renderer.measureBounds(g2, geoInputBox, textFont, labelDesc);

		if (hasWindowResized(labelTop, view.getHeight())) {
			keepBoxOffscreen(view.getHeight());
		}

		handlePaddings();
	}

	private void handlePaddings() {
		bounds.setSize((int) (bounds.getWidth() - 2 * settings.getFixMargin()),
				(int) (bounds.getHeight() - 2 * settings.getFixMargin()));
	}

	private void keepBoxOffscreen(int viewHeight) {
		bounds.setLocation((int) bounds.getX(),
				(int) MyMath.clamp(bounds.getMinY(), 0,
						viewHeight - bounds.getHeight()));
	}

	private boolean hasWindowResized(double labelTop, int viewHeight) {
		return labelTop > 0 && labelTop < viewHeight;
	}

	/**
	 *
	 * @return y coord of the rectangle.
	 */
	public double getY() {
		return bounds.getY();
	}

	/**
	 * @param renderer to set.
	 */
	public void setRenderer(TextRenderer renderer) {
		this.renderer = renderer;
		settings = renderer.getSettings();
	}

	/**
	 *
	 * @param x coord.
	 * @param y coord.
	 * @return if bounds has (x, y) within.
	 */
	public boolean contains(int x, int y) {
		return bounds.contains(x, y);
	}
}
