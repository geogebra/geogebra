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
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;

/**
 * 
 * @author Michael
 */
public class GeoButton extends GeoElement implements TextProperties, Locateable,
		AbsoluteScreenLocateable {

	protected GeoPointND startPoint;

	private double width = 40.0;
	private double height = 30.0;

	private double fontSizeD = 1;
	private int fontStyle = GFont.PLAIN;

	private boolean serifFont = false;

	private boolean fixedSize = false;

	private Observer observer;

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
		this(cons);
		this.labelOffsetX = labelOffsetX;
		this.labelOffsetY = labelOffsetY;
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
	public void setStartPoint(GeoPointND p) {
		// remove old dependencies
		if (startPoint != null) {
			startPoint.getLocateableList().unregisterLocateable(this);
		}

		// set new location
		if (p == null) {
			if (startPoint != null) {
				startPoint = startPoint.copy();
			}

			labelOffsetX = 0;
			labelOffsetY = 0;
		} else {
			startPoint = p;

			// add new dependencies
			startPoint.getLocateableList().registerLocateable(this);
		}
	}

	@Override
	public void removeStartPoint(GeoPointND p) {
		// empty implementation.
	}

	@Override
	public GeoPointND getStartPoint() {
		return startPoint;
	}

	@Override
	public void setStartPoint(GeoPointND p, int number) {
		setStartPoint(p);
	}

	@Override
	public GeoPointND[] getStartPoints() {
		return new GeoPointND[] { startPoint };
	}

	@Override
	public void initStartPoint(GeoPointND p, int number) {
		startPoint = p;
	}

	@Override
	public boolean hasAbsoluteLocation() {
		return startPoint == null || startPoint.isAbsoluteStartPoint();
	}

	@Override
	public boolean isAlwaysFixed() {
		return false;
	}

	@Override
	public void setWaitForStartPoint() {
		// empty implementation
	}

	@Override
	public double getRealWorldLocX() {
		return startPoint == null ? 0 : startPoint.getInhomX();
	}

	@Override
	public double getRealWorldLocY() {
		return startPoint == null ? 0 : startPoint.getInhomY();
	}

	@Override
	public boolean isAbsoluteScreenLocActive() {
		return startPoint == null;
	}

	@Override
	public void setAbsoluteScreenLoc(int x, int y) {
		labelOffsetX = x;
		labelOffsetY = y;
		if (startPoint != null) {
			updateRelLocation(kernel.getApplication().getActiveEuclidianView());
		}
		if (!hasScreenLocation()) {
			setScreenLocation(x, y);
		}
	}

	@Override
	public int getAbsoluteScreenLocX() {
		return labelOffsetX;
	}

	@Override
	public int getAbsoluteScreenLocY() {
		return labelOffsetY;
	}

	@Override
	public void setAbsoluteScreenLocActive(boolean flag) {
		EuclidianView ev = kernel.getApplication().getActiveEuclidianView();
		if (flag && startPoint != null) {
			updateAbsLocation(ev);

			startPoint = null;
		} else if (!flag) {
			startPoint = new GeoPoint(cons);
			updateRelLocation(ev);
		}
	}

	/**
	 * Update absolute location according to relative location in given view
	 * (when rel. position active)
	 *
	 * @param ev
	 *            view
	 */
	public void updateAbsLocation(EuclidianView ev) {
		if (startPoint != null) {
			labelOffsetX = ev.toScreenCoordX(startPoint.getInhomX());
			labelOffsetY = ev.toScreenCoordY(startPoint.getInhomY());
		}
	}

	@Override
	public void setRealWorldLoc(double x, double y) {
		startPoint = new GeoPoint(cons);
		startPoint.setCoords(x, y, 1);
	}

	/**
	 * @return total screen width, overridden in GeoInputBox
	 */
	@Override
	public int getTotalWidth(EuclidianViewInterfaceCommon ev) {
		return getWidth();
	}

	@Override
	public int getTotalHeight(EuclidianViewInterfaceCommon ev) {
		return getHeight();
	}

	private void updateRelLocation(EuclidianView ev) {
		startPoint.setCoords(ev.toRealWorldCoordX(labelOffsetX),
				ev.toRealWorldCoordY(labelOffsetY), 1);
	}

	@Override
	public void updateLocation() {
		update();
	}

	/**
	 * @return width in pixels (if it's fixed)
	 */
	public int getWidth() {
		return (int) width;
	}

	/**
	 * @param width
	 *            width
	 */
	public void setWidth(double width) {
		this.width = width;
		if (hasScreenLocation()) {
			getScreenLocation().initWidth((int) width);
		}
	}

	/**
	 *
	 * @return height in pixels (if it's fixed)
	 */
	public int getHeight() {
		return (int) height;
	}

	/**
	 * @param height
	 *            height in pixels
	 */
	public void setHeight(double height) {
		this.height = height;
		if (hasScreenLocation()) {
			getScreenLocation().initHeight((int) height);
		}
	}

	@Override
	public ValueType getValueType() {
		return ValueType.VOID;
	}

	@Override
	public void setUndefined() {
		// do nothing
	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public boolean showInEuclidianView() {
		return true;
	}

	@Override
	final public boolean isAlgebraViewEditable() {
		return !isIndependent();
	}

	@Override
	public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return "";
	}

	/**
	 *
	 * @param ev
	 *            the euclidian view.
	 * @return x coordinate of screen location.
	 */
	public int getScreenLocX(EuclidianViewInterfaceCommon ev) {
		return startPoint == null ? labelOffsetX : ev.toScreenCoordX(startPoint.getInhomX());
	}

	/**
	 *
	 * @param ev
	 *            the euclidian view.
	 * @return y coordinate of screen location.
	 */
	public int getScreenLocY(EuclidianViewInterfaceCommon ev) {
		return startPoint == null ? labelOffsetY : ev.toScreenCoordY(startPoint.getInhomY());
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
	public boolean isAbsoluteScreenLocateable() {
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
			startPoint.appendStartPointXML(sb);
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
		ScreenReaderBuilder sb = new ScreenReaderBuilder(kernel.getLocalization());
		addAuralName(sb);
		sb.append(" ");
		sb.appendMenuDefault("Pressed", "pressed");
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
		if (bounds == null) {
			return;
		}

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