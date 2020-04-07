package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RotatableBoundingBox;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFormula;

public class DrawFormula extends Drawable implements DrawMedia {

	private final TransformableRectangle rectangle;
	private GFont textFont;

	/**
	 * @param ev view
	 * @param equation equation
	 */
	public DrawFormula(EuclidianView ev, GeoFormula equation) {
		super(ev, equation);
		textFont = ev.getFont();
		this.rectangle = new TransformableRectangle(view, equation);
		update();
	}

	@Override
	public void update() {
		updateStrokes(geo);
		labelDesc = geo.toValueString(StringTemplate.defaultTemplate);
		rectangle.updateSelfAndBoundingBox();
	}

	@Override
	public void draw(GGraphics2D g2) {
		g2.setPaint(geo.getObjectColor());
		g2.setFont(textFont);
		g2.setStroke(objStroke); // needed eg for \sqrt
		g2.saveTransform();
		g2.transform(rectangle.getDirectTransform());
		drawMultilineLaTeX(g2, textFont, geo.getObjectColor(),
				view.getBackgroundCommon());
		g2.restoreTransform();
	}

	@Override
	public GRectangle getBounds() {
		return rectangle.getBounds();
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
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public RotatableBoundingBox getBoundingBox() {
		return rectangle.getBoundingBox();
	}

	@Override
	public void updateByBoundingBoxResize(GPoint2D point, EuclidianBoundingBoxHandler handler) {
		rectangle.updateByBoundingBoxResize(point, handler);
	}

	@Override
	public void updateContent() {
		// TODO update editor content
	}

	@Override
	public void toForeground(int i, int i1) {
		update(); // TODO this just shows bounding box; start editing instead
		view.getApplication().storeUndoInfo();
	}
}
