package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hits3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

public final class DrawText3D extends Drawable3DCurves {

	private boolean wasLaTeX;

	/**
	 * @param a_view3d
	 *            view
	 * @param text
	 *            text
	 */
	public DrawText3D(EuclidianView3D a_view3d, GeoText text) {
		super(a_view3d, text);
		wasLaTeX = text.isLaTeX();
		((DrawLabel3DForText) label).setGeo(text);

		setPickingType(PickingType.POINT_OR_CURVE);
	}

	@Override
	protected DrawLabel3D newDrawLabel3D(EuclidianView3D view3D) {
		return new DrawLabel3DForText(view3D, this);
	}

	@Override
	protected void updateForView() {
		// no change on zoom / rotate
	}

	@Override
	protected boolean updateForItSelf() {
		// setLabelWaitForUpdate();
		return true;
	}

	@Override
	public void drawGeometry(Renderer renderer) {
		// see drawlabel
	}

	@Override
	protected void updateLabel() {
		if (getView3D().drawsLabels()) {
			GeoText text = (GeoText) getGeoElement();
			if (wasLaTeX != text.isLaTeX()) {
				label.setWaitForReset();
			}
			wasLaTeX = text.isLaTeX();
			label.update(text.getTextStringSafe(), getFont(),
					getGeoElement().getBackgroundColor(),
					getGeoElement().getObjectColor(), getLabelPosition(),
					getLabelOffsetX(), -getLabelOffsetY(), 0);
		}
	}

	@Override
	protected void updateLabelPosition() {
		label.updatePosition(getView3D().getRenderer());
	}

	private GFont getFont() {
		GeoText text = (GeoText) getGeoElement();

		// text's font size is relative to the global font size
		int newFontSize = (int) Math.max(4,
				getView3D().getFontSize() * text.getFontSizeMultiplier());
		int newFontStyle = text.getFontStyle();
		boolean newSerifFont = text.isSerifFont();

		/*
		 * if (textFont.canDisplayUpTo(text.getTextString()) != -1 || fontSize
		 * !=newFontSize || fontStyle != newFontStyle || newSerifFont !=
		 * serifFont) { super.updateFontSize();
		 * 
		 * fontSize = newFontSize; fontStyle = newFontStyle; serifFont =
		 * newSerifFont;
		 */

		// if (isLaTeX) {
		// //setEqnFontSize();
		// } else {
		GFont textFont = getView3D().getApplication().getFontCanDisplay(
				text.getTextString(), newSerifFont, newFontStyle, newFontSize);
		// }

		return textFont;
	}

	// private boolean isLocationDefined;

	@Override
	protected boolean isLabelVisible() {
		return isVisible(); // && isLocationDefined;
	}

	@Override
	public Coords getLabelPosition() {

		// isLocationDefined = true;

		GeoText text = (GeoText) getGeoElement();

		// compute location of text
		if (text.isAbsoluteScreenLocActive()) {
			return new Coords(text.getAbsoluteScreenLocX(),
					text.getAbsoluteScreenLocY(), 0, 1);
		}

		GeoPointND loc = text.getStartPoint();

		if (loc == null) {
			// return new
			// Coords(getView3D().getXZero(),getView3D().getYZero(),0,1);
			return new Coords(0, 0, 0, 1);
		}

		if (!loc.isDefined()) {
			// isLocationDefined = false;
			return null;
		}

		return loc.getInhomCoordsInD3();
	}

	@Override
	public int getPickOrder() {

		return DRAW_PICK_ORDER_TEXT;
	}

	@Override
	public boolean hit(Hitting hitting) {

		if (hitting.hitLabel(label)) {
			setZPick(label.getDrawZ(), label.getDrawZ(),
					hitting.discardPositiveHits(), -label.getDrawZ());
			return true;
		}

		return false;
	}

	@Override
	protected boolean hitLabel(Hitting hitting, Hits3D hits) {
		return false; // no label
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_TEXTS);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_TEXTS);
	}

	/**
	 * Draw this or not, depending on abs flag.
	 * 
	 * @param renderer
	 *            renderer
	 * @param absolute
	 *            whetther only absolute or only relative text should be painted
	 */
	public void drawForAbsolutePosition(Renderer renderer, boolean absolute) {
		GeoText text = (GeoText) getGeoElement();
		if (absolute == text.isAbsoluteScreenLocActive()) {
			drawLabel(renderer);
		}
	}

}
