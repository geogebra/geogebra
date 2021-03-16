/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MyButton;
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
	public MyButton myButton;

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
		myButton = new MyButton(geoButton, view);

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

		labelRectangle.setBounds(xLabel, yLabel, myButton.getWidth(),
				myButton.getHeight());

		myButton.setBounds(labelRectangle);
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (isVisible) {
			myButton.setSelected(isHighlighted());
			myButton.paintComponent(g2, geoButton.getFontSizeMultiplier(),
					true);
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

}
