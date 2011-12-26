/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

 */

/*
 * Drawable.java
 *
 * Created on 13. Oktober 2001, 17:40
 */

package geogebra.euclidian;

import geogebra.common.euclidian.DrawableND;
import geogebra.common.euclidian.EuclidianStyleConstants;
import geogebra.common.euclidian.EuclidianViewInterface2D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.AbstractApplication;

import geogebra.common.awt.BasicStroke;
import geogebra.common.awt.Color;
import geogebra.common.awt.Dimension;
import geogebra.common.awt.Font;
import geogebra.common.awt.Graphics2D;
import geogebra.common.awt.Point;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Shape;
import geogebra.common.factories.AwtFactory;
import geogebra.common.awt.Area;

/**
 * 
 * @author Markus
 * @version
 */
public abstract class Drawable extends DrawableND {

	private boolean forceNoFill;

	BasicStroke objStroke = EuclidianStatic.getDefaultStroke();
	BasicStroke selStroke = EuclidianStatic.getDefaultSelectionStroke();
	BasicStroke decoStroke = EuclidianStatic.getDefaultStroke();

	private int lineThickness = -1;
	public int lineType = -1;

	protected EuclidianViewInterface2D view;
	protected int hitThreshold = 3;
	protected GeoElement geo;
	public int xLabel, yLabel;
	/** for Previewables */
	int mouseX, mouseY;
	/** label Description */
	protected String labelDesc;
	private String oldLabelDesc;
	private boolean labelHasIndex = false;
	/** for label hit testing */
	Rectangle labelRectangle = AwtFactory.prototype.newRectangle(0, 0);
	Shape strokedShape, strokedShape2;

	private Area shape;

	private int lastFontSize = -1;

	/** tracing */
	protected boolean isTracing = false;

	// boolean createdByDrawList = false;

	@Override
	public abstract void update();

	public abstract void draw(Graphics2D g2);

	public abstract boolean hit(int x, int y);

	public abstract boolean isInside(Rectangle rect);

	@Override
	public abstract GeoElement getGeoElement();

	public abstract void setGeoElement(GeoElement geo);

	public double getxLabel() {
		return xLabel;
	}

	public double getyLabel() {
		return yLabel;
	}

	void updateFontSize() {
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 * 
	 * @return null when this Drawable is infinite or undefined
	 */
	public Rectangle getBounds() {
		return null;
	}

	final protected void drawLabel(Graphics2D g2) {
		if (labelDesc == null)
			return;
		String label = labelDesc;
		Font oldFont = null;

		// allow LaTeX caption surrounded by $ $
		if (label.startsWith("$") && label.endsWith("$")) {
			boolean serif = true; // nice "x"s
			if (geo.isGeoText())
				serif = ((GeoText) geo).isSerifFont();
			int offsetY = 10 + view.getFontSize(); // make sure LaTeX labels don't go
												// off bottom of screen
			view.getApplication().getDrawEquation();
			Dimension dim = DrawEquation.drawEquation(geo.getKernel()
					.getApplication(), geo, g2, xLabel, yLabel - offsetY, label
					.substring(1, label.length() - 1), g2.getFont(), serif, g2
					.getColor(), g2.getBackground(), true);
			labelRectangle.setBounds(xLabel, yLabel - offsetY, (int)dim.getWidth(),
					(int)dim.getHeight());
			return;
		}

		// label changed: check for bold or italic tags in caption
		if (oldLabelDesc != labelDesc || labelDesc.startsWith("<")) {
			boolean italic = false;

			// support for bold and italic tags in captions
			// must be whole caption
			if (label.startsWith("<i>") && label.endsWith("</i>")) {
				if (oldFont == null) {
					oldFont = g2.getFont();
				}

				// use Serif font so that we can get a nice curly italic x
				g2.setFont(view.getApplication().getFontCommon(true,
						oldFont.getStyle() | Font.ITALIC, oldFont.getSize()));
				label = label.substring(3, label.length() - 4);
				italic = true;
			}

			if (label.startsWith("<b>") && label.endsWith("</b>")) {
				if (oldFont == null)
					oldFont = g2.getFont();

				g2.setFont(g2.getFont().deriveFont(
						Font.BOLD + (italic ? Font.ITALIC : 0)));
				label = label.substring(3, label.length() - 4);
			}
		}

		// no index in label: draw it fast
		int fontSize = g2.getFont().getSize();
		if (oldLabelDesc == labelDesc && !labelHasIndex
				&& lastFontSize == fontSize) {
			lastFontSize = fontSize;
			g2.drawString(label, xLabel, yLabel);
			labelRectangle.setLocation(xLabel, yLabel - fontSize);
		} else { // label with index or label has changed:
					// do the slower index drawing routine and check for indices
			oldLabelDesc = labelDesc;

			Point p = EuclidianStatic.drawIndexedString(view.getApplication(),
					g2, label, xLabel, yLabel, isSerif());
			labelHasIndex = p.y > 0;
			labelRectangle.setBounds(xLabel, yLabel - fontSize, p.x, fontSize
					+ p.y);
		}

		if (oldFont != null)
			g2.setFont(oldFont);
	}

	/**
	 * Adapts xLabel and yLabel to make sure that the label rectangle fits fully
	 * on screen.
	 */
	final public void ensureLabelDrawsOnScreen() {
		// draw label and
		drawLabel(view.getTempGraphics2D(view.getApplication().getPlainFontCommon()));

		// make sure labelRectangle fits on screen horizontally
		if (xLabel < 3)
			xLabel = 3;
		else
			xLabel = Math.min(xLabel, view.getWidth() - (int)labelRectangle.getWidth() - 3);
		if (yLabel < labelRectangle.getHeight())
			yLabel = (int)labelRectangle.getHeight();
		else
			yLabel = Math.min(yLabel, view.getHeight() - 3);

		// update label rectangle position
		labelRectangle.setLocation(xLabel, yLabel - view.getFontSize());
	}

	/*
	 * old version final void drawMultilineLaTeX(Graphics2D g2, Font font, Color
	 * fgColor, Color bgColor) {
	 * 
	 * int fontSize = g2.getFont().getSize(); float lineSpread = fontSize *
	 * 1.0f; float lineSpace = fontSize * 0.5f;
	 * 
	 * int maxhOffset=0; float height=0;
	 * 
	 * Dimension dim;
	 * 
	 * labelDesc=labelDesc.replaceAll("\\$\\$", "\\$"); // replace $$ with $
	 * labelDesc=labelDesc.replaceAll("\\\\\\[", "\\$");// replace \[ with $
	 * labelDesc=labelDesc.replaceAll("\\\\\\]", "\\$");// replace \] with $
	 * labelDesc=labelDesc.replaceAll("\\\\\\(", "\\$");// replace \( with $
	 * labelDesc=labelDesc.replaceAll("\\\\\\)", "\\$");// replace \) with $
	 * 
	 * 
	 * String[] lines=labelDesc.split("\n");
	 * 
	 * 
	 * for (int k=0 ; k<lines.length ; k++) {
	 * 
	 * String[] strings=lines[k].split("\\$"); int heights[] = new
	 * int[strings.length];
	 * 
	 * boolean latex=false; if (lines[k].indexOf('$') == -1 && lines.length ==
	 * 1) { latex=true; // just latex }
	 * 
	 * int maxHeight=0; // calculate heights of each element for (int j=0 ;
	 * j<strings.length ; j++) {
	 * 
	 * if (!strings[j].equals(str(" ",strings[j].length()))) // check not empty
	 * or just spaces { if (latex) { dim =
	 * drawEquation(view.getTempGraphics2D(),0,0, strings[j], font, fgColor,
	 * bgColor); //dim = sHotEqn.getSizeof(strings[j]); //widths[j] = dim.width;
	 * heights[j] = dim.height; } else { heights[j] = (int)lineSpread; //p.y; }
	 * } else { heights[j]=0; } latex=!latex; if (heights[j] > maxHeight)
	 * maxHeight=heights[j];
	 * 
	 * }
	 * 
	 * if (k!=0) maxHeight += lineSpace;
	 * 
	 * int hOffset=0;
	 * 
	 * latex=false; if (lines[k].indexOf('$') == -1 && lines.length == 1) {
	 * latex=true; // just latex //Application.debug("just latex"); }
	 * 
	 * // draw elements for (int j=0 ; j<strings.length ; j++) {
	 * 
	 * if (!strings[j].equals(str(" ",strings[j].length()))) // check not empty
	 * or just spaces {
	 * 
	 * int vOffset = (maxHeight - heights[j] )/2; // vertical centering
	 * 
	 * if (latex) {
	 * 
	 * dim = drawEquation(g2,xLabel + hOffset,(int)(yLabel + height) + vOffset,
	 * strings[j], font, fgColor, bgColor); hOffset+=dim.width; } else { Point p
	 * = drawIndexedString(g2, strings[j], xLabel + hOffset, yLabel + height +
	 * vOffset + lineSpread); hOffset+=p.x; } } latex=!latex; } if (hOffset >
	 * maxhOffset) maxhOffset = hOffset; height += maxHeight; }
	 * labelRectangle.setBounds(xLabel, yLabel, maxhOffset, (int)height); }
	 * 
	 * // returns a string consisting of n consecutive "str"s final private
	 * String str(String str, int n) { if (n == 0) return ""; else if (n == 1)
	 * return str; else { StringBuilder ret = new StringBuilder();
	 * 
	 * for (int i=0 ; i<n ; i++) ret.append(str); return ret.toString(); } }
	 */

	public final void drawMultilineLaTeX(Graphics2D g2, Font font,
			Color fgColor, Color bgColor) {
		labelRectangle.setBounds(EuclidianStatic.drawMultilineLaTeX(
				view.getApplication(), view.getTempGraphics2D(font), geo, g2,
				font, fgColor, bgColor, labelDesc, xLabel, yLabel, isSerif()));
	}

	/*
	 * private static geogebra.gui.hoteqn.sHotEqn eqn;
	 * 
	 * final public static Dimension drawEquationHotEqn(Application app,
	 * Graphics2D g2, int x, int y, String text, Font font, Color fgColor, Color
	 * bgColor) { Dimension dim; if (eqn == null) { eqn = new
	 * geogebra.gui.hoteqn.sHotEqn(text); //Application.debug(eqn.getSize());
	 * eqn.setDoubleBuffered(false); eqn.setEditable(false);
	 * eqn.removeMouseListener(eqn); eqn.removeMouseMotionListener(eqn);
	 * eqn.setDebug(false); eqn.setOpaque(false); } else {
	 * eqn.setEquation(text); }
	 * 
	 * //setEqnFontSize(); int size = (font.getSize() / 2) * 2; if (size < 10)
	 * size = 10; else if (size > 28) size = 28;
	 * 
	 * eqn.setFontname(font.getName()); eqn.setFontsizes(size, size - 2, size -
	 * 4, size - 6); eqn.setFontStyle(font.getStyle());
	 * 
	 * 
	 * eqn.setForeground(fgColor); eqn.setBackground(bgColor);
	 * 
	 * 
	 * //eqn.paintComponent(g2Dtemp,0,0); //dim=eqn.getSizeof(text);
	 * eqn.paintComponent(g2,x,y); dim=eqn.getSize();
	 * 
	 * //Application.debug(size); return dim; }//
	 */

	final boolean isSerif() {
		return geo.isGeoText() ? ((GeoText) geo).isSerifFont() : false;
	}

	final void drawMultilineText(Graphics2D g2) {

		if (labelDesc == null)
			return;

		// no index in text
		if (oldLabelDesc == labelDesc && !labelHasIndex) {

			labelRectangle.setBounds(EuclidianStatic.drawMultiLineText(
					view.getApplication(), labelDesc, xLabel, yLabel, g2,
					isSerif()));
		} else {
			int lines = 0;
			int fontSize = g2.getFont().getSize();
			float lineSpread = fontSize * 1.5f;

			int xoffset = 0, yoffset = 0;
			// text with indices
			// label description has changed, search for possible indices
			oldLabelDesc = labelDesc;

			// draw text line by line
			int lineBegin = 0;
			int length = labelDesc.length();
			xoffset = 0;
			yoffset = 0;
			for (int i = 0; i < length - 1; i++) {
				if (labelDesc.charAt(i) == '\n') {
					// end of line reached: draw this line
					Point p = EuclidianStatic.drawIndexedString(
							view.getApplication(), g2,
							labelDesc.substring(lineBegin, i), xLabel, yLabel
									+ lines * lineSpread, isSerif());
					if (p.x > xoffset)
						xoffset = p.x;
					if (p.y > yoffset)
						yoffset = p.y;
					lines++;
					lineBegin = i + 1;
				}
			}

			float ypos = yLabel + lines * lineSpread;
			Point p = EuclidianStatic
					.drawIndexedString(view.getApplication(), g2,
							labelDesc.substring(lineBegin), xLabel, ypos,
							isSerif());
			if (p.x > xoffset)
				xoffset = p.x;
			if (p.y > yoffset)
				yoffset = p.y;
			labelHasIndex = yoffset > 0;
			int height = (int) ((lines + 1) * lineSpread);
			labelRectangle.setBounds(xLabel - 3, yLabel - fontSize - 3,
					xoffset + 6, height + 6);
		}
	}

	/**
	 * Adds geo's label offset to xLabel and yLabel.
	 * 
	 * @return whether something was changed
	 */
	final protected boolean addLabelOffset() {
		return addLabelOffset(false);
	}

	/**
	 * Adds geo's label offset to xLabel and yLabel.
	 * 
	 * @param ensureLabelOnScreen
	 *            if true we make sure that the label is drawn on screen
	 * 
	 * @return whether something was changed
	 */
	final protected boolean addLabelOffset(boolean ensureLabelOnScreen) {
		if (ensureLabelOnScreen) {
			// MAKE SURE LABEL STAYS ON SCREEN
			int xLabelOld = xLabel;
			int yLabelOld = yLabel;
			xLabel += geo.labelOffsetX;
			yLabel += geo.labelOffsetY;

			// change xLabel and yLabel so that label stays on screen
			ensureLabelDrawsOnScreen();

			// something changed?
			return xLabelOld != xLabel || yLabelOld != yLabel;
		}

		// STANDARD BEHAVIOUR
		if (geo.labelOffsetX == 0 && geo.labelOffsetY == 0)
			return false;

		int x = xLabel + geo.labelOffsetX;
		int y = yLabel + geo.labelOffsetY;

		// don't let offset move label out of screen
		int xmax = view.getWidth() - 15;
		int ymax = view.getHeight() - 5;
		if (x < 5 || x > xmax)
			return false;
		if (y < 15 || y > ymax)
			return false;

		xLabel = x;
		yLabel = y;
		return true;
	}

	/**
	 * Was the label clicked at? (mouse pointer location (x,y) in screen coords)
	 */
	public boolean hitLabel(int x, int y) {
		return labelRectangle.contains(x, y);
	}

	private boolean forcedLineType;

	/**
	 * Set fixed line type and ignore line type of the geo. Needed for
	 * inequalities.
	 * 
	 * @param type
	 */
	final void forceLineType(int type) {
		forcedLineType = true;
		lineType = type;
	}

	/**
	 * Update strokes (default,selection,deco) accordingly to geo
	 * 
	 * @param geo
	 */
	final void updateStrokes(GeoElement geo) {
		strokedShape = null;
		strokedShape2 = null;

		if (lineThickness != geo.lineThickness) {
			lineThickness = geo.lineThickness;
			if (!forcedLineType)
				lineType = geo.lineType;

			float width = lineThickness / 2.0f;
			objStroke = EuclidianStatic.getStroke(width, lineType);
			decoStroke = EuclidianStatic.getStroke(width,
					EuclidianStyleConstants.LINE_TYPE_FULL);
			selStroke = EuclidianStatic.getStroke(width
					+ EuclidianStyleConstants.SELECTION_ADD,
					EuclidianStyleConstants.LINE_TYPE_FULL);
		} else if (lineType != geo.lineType) {
			if (!forcedLineType)
				lineType = geo.lineType;

			float width = lineThickness / 2.0f;
			objStroke = EuclidianStatic.getStroke(width, lineType);
		}
	}

	// private StringBuilder command = new StringBuilder();
	private double[] coords = new double[2];

	// =================================================================
	// G.Sturr 2010-5-14: new recordToSpreadsheet method

	public void recordToSpreadsheet(GeoElement geo) {

		// stop spurious numbers after undo
		if (view.getKernel().isViewReiniting())
			return;
		Construction cons = view.getKernel().getConstruction();
		if (cons.getApplication().isUsingFullGui())
			cons.getApplication()
					.traceToSpreadsheet(geo);
	}

	protected void fill(Graphics2D g2, Shape shape, boolean usePureStroke) {
		if (isForceNoFill())
			return;
		if (geo.getFillType() == GeoElement.FILL_HATCH) {

			// use decoStroke as it is always full (not dashed/dotted etc)
			geogebra.common.euclidian.HatchingHandler.setHatching(g2, decoStroke,
					geo.getObjectColor(),
					geo.getBackgroundColor(),
					geo.getAlphaValue(), geo.getHatchingDistance(),
					geo.getHatchingAngle());
			if (usePureStroke)
				EuclidianStatic.fillWithValueStrokePure(shape, g2);
			else
				g2.fill(shape);

		} else if (geo.getFillType() == GeoElement.FILL_IMAGE) {
			geogebra.common.euclidian.HatchingHandler.setTexture(g2, geo, geo.getAlphaValue());
			g2.fill(shape);
		} else if (geo.getAlphaValue() > 0.0f) {
			g2.setPaint(geo.getFillColor());
			g2.fill(shape);
		}

	}

	/**
	 * @param forceNoFill
	 *            the forceNoFill to set
	 */
	public void setForceNoFill(boolean forceNoFill) {
		this.forceNoFill = forceNoFill;
	}

	/**
	 * @return the forceNoFill
	 */
	public boolean isForceNoFill() {
		return forceNoFill;
	}

	/**
	 * @param shape
	 *            the shape to set
	 */
	public void setShape(Area shape) {
		this.shape = shape;
	}

	/**
	 * @return the shape
	 */
	public Area getShape() {
		return shape;
	}

}
