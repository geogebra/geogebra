/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;

/**
 * 
 * @author Michael
 */
public class GeoButton extends GeoWidget
		implements TextProperties, Furniture {

	private double fontSizeD = 1;
	private int fontStyle = GFont.PLAIN;

	private boolean serifFont = false;

	private boolean fixedSize = false;

	private Observer observer;

	// original positions and widths
	// set once (if null)

	/**
	 * Creates new button
	 * 
	 * @param c
	 *            construction
	 */
	public GeoButton(Construction c) {
		super(c);

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		setEuclidianVisible(true);
		setAuxiliaryObject(true);
	}

	/**
	 * Creates new button
	 * 
	 * @param cons
	 *            construction
	 * @param labelOffsetX
	 *            x offset
	 * @param labelOffsetY
	 *            y offset
	 */
	public GeoButton(Construction cons, int labelOffsetX, int labelOffsetY) {
		super(cons, labelOffsetX, labelOffsetY);
	}

	/**
	 * @param cons
	 *            cons
	 * @param size
	 *            size multiplier, usually 2
	 */
	public GeoButton(Construction cons, double size) {
		this(cons);
		this.fontSizeD = size;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_BUTTON_ACTION;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.BUTTON;
	}

	@Override
	public GeoElement copy() {
		return new GeoButton(cons, labelOffsetX, labelOffsetY);
	}

	@Override
	public final boolean isGeoButton() {
		return true;
	}

	@Override
	public void set(GeoElementND geo) {
		if (!geo.isGeoButton()) {
			return;
		}
		setCaption(geo.getRawCaption());
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return label;
	}

	/**
	 * @return true for textfields, false for buttons
	 */
	public boolean isTextField() {
		return false;
	}

	/**
	 * Returns whether the value (e.g. equation) should be shown as part of the
	 * label description
	 */
	@Override
	final public boolean isLabelValueShowable() {
		return false;
	}

	@Override
	public double getFontSizeMultiplier() {
		return fontSizeD;
	}

	@Override
	public void setFontSizeMultiplier(double d) {
		fontSizeD = d;
	}

	@Override
	public int getFontStyle() {
		return fontStyle;
	}

	@Override
	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}

	@Override
	public int getPrintDecimals() {
		return 0;
	}

	@Override
	public int getPrintFigures() {
		return 0;
	}

	@Override
	public void setPrintDecimals(int printDecimals, boolean update) {
		// do nothing
	}

	@Override
	public void setPrintFigures(int printFigures, boolean update) {
		// do nothing
	}

	@Override
	public boolean isSerifFont() {
		return serifFont;
	}

	@Override
	public void setSerifFont(boolean serifFont) {
		this.serifFont = serifFont;
	}

	@Override
	public boolean useSignificantFigures() {
		return false;
	}

	@Override
	public boolean justFontSize() {
		return true;
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		// font settings
		GeoText.appendFontTag(sb, serifFont, fontSizeD, fontStyle, false,
				kernel.getApplication());

		// name of image file
		if (getFillImage() != null) {
			sb.append("\t<file name=\"");
			sb.append(StringUtil
					.encodeXML(this.getGraphicsAdapter().getImageFileName()));
			sb.append("\"/>\n");
		}
		if (isFixedSize()) {
			XMLBuilder.dimension(sb, Integer.toString(getWidth()), Integer.toString(getHeight()));
		}
		if (!isAbsoluteScreenLocActive()) {
			sb.append("\t<startPoint x=\"");
			sb.append(getRealWorldLocX());
			sb.append("\" y=\"");
			sb.append(getRealWorldLocY());
			sb.append("\" z=\"1\"/>");
		}
	}

	@Override
	public boolean hasBackgroundColor() {
		return true;
	}

	@Override
	public boolean isFillable() {
		return true;
	}

	@Override
	public FillType getFillType() {
		return FillType.IMAGE;
	}

	@Override
	public boolean isFurniture() {
		return true;
	}

	@Override
	public boolean isPinnable() {
		return true;
	}

	/**
	 * @return whther this button has fixed size (otherwise depends on text)
	 */
	public boolean isFixedSize() {
		return fixedSize;
	}

	/**
	 * @param fixedSize
	 *            whether this button should have fixed size
	 */
	public void setFixedSize(boolean fixedSize) {
		this.fixedSize = fixedSize;
		if (observer != null) {
			observer.notifySizeChanged();
		}
	}

	/**
	 * @param observer
	 *            object watching size of this button
	 */
	public void setObserver(Observer observer) {
		this.observer = observer;
	}

	/** Object watching size of a button */
	public interface Observer {
		/**
		 * This method is called when size is changed
		 */
		public void notifySizeChanged();
	}

	@Override
	public boolean isLaTeXTextCommand() {
		return false;
	}

	/**
	 * @param cons
	 *            cons
	 * @return new button (with default size)
	 */
	public static GeoButton getNewButton(Construction cons) {
		return new GeoButton(cons, ConstructionDefaults.DEFAULT_BUTTON_SIZE);
	}

	@Override
	public GColor getBackgroundColor() {
		if (bgColor == null && colFunction == null) {
			return null;
		}

		// get dynamic Opacity from *foreground* color
		if (colFunction != null && colFunction.size() == 4) {
			double alpha = colFunction.get(3).evaluateDouble();
			int r = 255;
			int g = 255;
			int b = 255;
			if (bgColor != null) {
				r = bgColor.getRed();
				g = bgColor.getGreen();
				b = bgColor.getBlue();
			}

			return GColor.newColor(r, g, b, (int) (alpha * 255));
		}

		return bgColor;
	}

	@Override
	public void addAuralStatus(Localization loc, ScreenReaderBuilder sb) {
		sb.append(loc.getMenuDefault("Selected", "selected"));
	}

	@Override
	public String getAuralTextForSpace() {
		Localization loc = kernel.getLocalization();
		ScreenReaderBuilder sb = new ScreenReaderBuilder();
		addAuralName(loc, sb);
		sb.append(" ");
		sb.append(loc.getMenuDefault("Pressed", "pressed"));
		sb.append(".");
		return sb.toString();
	}

	@Override
	public void setNeedsUpdatedBoundingBox(boolean b) {
		//
	}

	/**
	 * For Input Boxes and Buttons
	 * 
	 * @param pt
	 *            point to set
	 * @param cornerNumber
	 *            1,2,3,4,5
	 */
	@Override
	public void calculateCornerPoint(GeoPoint pt, int cornerNumber) {
		EuclidianView ev = kernel.getApplication().getEuclidianView1();
		DrawableND drawer = ev.getDrawableFor(this);

		if (!(drawer instanceof Drawable)) {
			// file loading (null) or 3D (Drawable3D)
			pt.setUndefined();
			return;
		}
		GRectangle bounds = ((Drawable) drawer).getBounds();

		double x, y;

		switch (cornerNumber) {
		default:
		case 1:
			x = bounds.getMinX();
			y = bounds.getMaxY();
			break;
		case 2:
			x = bounds.getMaxX();
			y = bounds.getMaxY();
			break;
		case 3:
			x = bounds.getMaxX();
			y = bounds.getMinY();
			break;
		case 4:
			x = bounds.getMinX();
			y = bounds.getMinY();
			break;
		case 5:
			pt.setCoords(bounds.getMaxX() - bounds.getMinX(),
					bounds.getMaxY() - bounds.getMinY(), 1);
			return;
		}

		pt.setCoords(ev.toRealWorldCoordX(x), ev.toRealWorldCoordY(y), 1);
	}

}