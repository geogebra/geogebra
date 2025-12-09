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

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.DrawButtonWidget;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

/**
 * Button (for scripting)
 * 
 * @author Markus Hohenwarter
 */
public final class DrawButton extends Drawable {

	private GeoButton geoButton;

	private boolean isVisible;

	private String oldCaption;
	/** button "component" */
	private final DrawButtonWidget myButton;

	/**
	 * @param view
	 *            view
	 * @param geoButton
	 *            button
	 */
	public DrawButton(EuclidianView view, GeoButton geoButton) {
		this.view = view;
		this.geoButton = geoButton;
		geo = geoButton;
		myButton = new DrawButtonWidget(geoButton, view);

		update();
	}

	@Override
	public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible) {
			return;
		}

		// get caption to show r
		String caption = geo.getCaption(StringTemplate.defaultTemplate);
		if (!caption.equals(oldCaption)) {
			oldCaption = caption;
			labelDesc = GeoElement.indicesToHTML(caption, true);
		}
		myButton.setText(labelDesc);

		int fontSize = (int) (view.getFontSize()
				* geoButton.getFontSizeMultiplier());
		App app = view.getApplication();

		// myButton.setOpaque(true);
		myButton.setFont(app.getFontCanDisplay(myButton.getText(),
				geoButton.isSerifFont(), geoButton.getFontStyle(), fontSize));

		xLabel = geoButton.getScreenLocX(view);
		yLabel = geoButton.getScreenLocY(view);
		myButton.preparePaint(geoButton.getFontSizeMultiplier(), true);
		labelRectangle.setBounds(xLabel, yLabel, myButton.getWidth(),
				myButton.getHeight());

		myButton.setBounds(labelRectangle);
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (isVisible) {
			myButton.setSelected(geoButton.doHighlighting());
			myButton.paintComponent(g2);
		}
	}

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */
	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return myButton.getBounds().contains(x, y) && isVisible;
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(labelRectangle);
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return myButton.getBounds().intersects(rect) && isVisible;
	}

	/**
	 * Returns false
	 */
	@Override
	public boolean hitLabel(int x, int y) {
		return false;
	}

	@Override
	public GRectangle getBounds() {
		return myButton.getBounds();
	}

	public DrawButtonWidget getWidget() {
		return myButton;
	}
}
