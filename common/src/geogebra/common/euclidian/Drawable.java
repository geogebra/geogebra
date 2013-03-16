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

package geogebra.common.euclidian;

import geogebra.common.awt.GArea;
import geogebra.common.awt.GBasicStroke;
import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GPoint;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GShape;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.kernelND.GeoElementND;
import geogebra.common.main.App;
import geogebra.common.plugin.EuclidianStyleConstants;


/**
 * 
 * @author Markus
 */
public abstract class Drawable extends DrawableND {

	private boolean forceNoFill;

	/**
	 * Default stroke for this drawable
	 */
	protected GBasicStroke objStroke = EuclidianStatic.getDefaultStroke();
	/**
	 * Stroke for this drawable in case referenced geo is selected
	 */
	protected GBasicStroke selStroke = EuclidianStatic.getDefaultSelectionStroke();
	/**
	 * Stroke for decorations; always full
	 */
	protected GBasicStroke decoStroke = EuclidianStatic.getDefaultStroke();

	private int lineThickness = -1;
	private int lineType = -1;

	/**
	 * View in which this is drawn
	 */
	protected EuclidianView view;
	/**
	 * Hit tolerance
	 */
	protected int hitThreshold = 3;
	/**
	 * Referenced GeoElement
	 */
	protected GeoElement geo;
	/** x-coord of the label */
	public int xLabel;
	/** y-coord of the label */
	public int yLabel;
	/** for Previewables */
	int mouseX;
	/** for Previewables */
	int mouseY;
	/** label Description */
	protected String labelDesc;
	private String oldLabelDesc;
	private boolean labelHasIndex = false;
	/** for label hit testing */
	protected GRectangle labelRectangle = AwtFactory.prototype.newRectangle(0, 0);
	/**
	 * Stroked shape for hits testing of conics, loci ... with alpha = 0
	 */
	protected GShape strokedShape;
	/**
	 * Stroked shape for hits testing of hyperbolas
	 */
	protected GShape strokedShape2;

	private GArea shape;

	private int lastFontSize = -1;

	/** tracing */
	protected boolean isTracing = false;

	// boolean createdByDrawList = false;

	@Override
	public abstract void update();

	/**
	 * Draws this drawable to given graphics
	 * @param g2 graphics
	 */
	public abstract void draw(GGraphics2D g2);

	/**
	 * @param x mouse x-coord
	 * @param y mouse y-coord
	 * @return true if hit
	 */
	public abstract boolean hit(int x, int y);

	/**
	 * @param rect rectangle
	 * @return true if the whole drawable is inside
	 */
	public abstract boolean isInside(GRectangle rect);
	
	/**
	 * @param rect rectangle
	 * @return true if a part of this Drawable is within the rectangle
	 */
	public boolean intersectsRectangle(GRectangle rect){
		GArea s=getShape();
		if (s==null){
			return false;
		}
		if (isFilled()){
			return s.intersects(rect);
		}
		return s.intersects(rect)&&!s.contains(rect);
	}

	@Override
	public abstract GeoElement getGeoElement();

	/**
	 * @param geo referenced geo
	 */
	public abstract void setGeoElement(GeoElement geo);

	@Override
	public double getxLabel() {
		return xLabel;
	}

	@Override
	public double getyLabel() {
		return yLabel;
	}

	/**
	 * Updates font size
	 */
	public void updateFontSize() {
		//do nothing, overriden in drawables
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 * 
	 * @return null when this Drawable is infinite or undefined
	 */
	public GRectangle getBounds() {
		return null;
	}

	/**
	 * Draws label of referenced geo
	 * @param g2 graphics
	 */
	final protected void drawLabel(GGraphics2D g2) {
		if (labelDesc == null)
			return;
		String label = labelDesc;
		GFont oldFont = null;

		// allow LaTeX caption surrounded by $ $
		if ((label.charAt(0)=='$') && label.endsWith("$")) {
			boolean serif = true; // nice "x"s
			if (geo.isGeoText())
				serif = ((GeoText) geo).isSerifFont();
			int offsetY = 10 + view.getFontSize(); // make sure LaTeX labels don't go
												// off bottom of screen
			App app = view.getApplication();
			GDimension dim = app.getDrawEquation().
					drawEquation(geo.getKernel()
					.getApplication(), geo, g2, xLabel, yLabel - offsetY, label
					.substring(1, label.length() - 1), g2.getFont(), serif, g2
					.getColor(), g2.getBackground(), true);
			labelRectangle.setBounds(xLabel, yLabel - offsetY, dim.getWidth(),
					dim.getHeight());
			return;
		}

		// label changed: check for bold or italic tags in caption
		if (oldLabelDesc != labelDesc || (labelDesc.charAt(0)=='<')) {
			boolean italic = false;

			// support for bold and italic tags in captions
			// must be whole caption
			if (label.startsWith("<i>") && label.endsWith("</i>")) {
				oldFont = g2.getFont();

				// use Serif font so that we can get a nice curly italic x
				g2.setFont(view.getApplication().getFontCommon(true,
						oldFont.getStyle() | GFont.ITALIC, oldFont.getSize()));
				label = label.substring(3, label.length() - 4);
				italic = true;
			}

			if (label.startsWith("<b>") && label.endsWith("</b>")) {
				oldFont = g2.getFont();

				g2.setFont(g2.getFont().deriveFont(
						GFont.BOLD + (italic ? GFont.ITALIC : 0)));
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

			GPoint p = EuclidianStatic.drawIndexedString(view.getApplication(),
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

	
	/**
	 * @param g2 graphics
	 * @param font font
	 * @param fgColor text color
	 * @param bgColor background color
	 */
	public final void drawMultilineLaTeX(GGraphics2D g2, GFont font,
			GColor fgColor, GColor bgColor) {
		labelRectangle.setBounds(EuclidianStatic.drawMultilineLaTeX(
				view.getApplication(), view.getTempGraphics2D(font), geo, g2,
				font, fgColor, bgColor, labelDesc, xLabel, yLabel, isSerif()));
	}

	
	/**
	 * @return true if serif font is used for GeoText
	 */
	final boolean isSerif() {
		return geo.isGeoText() ? ((GeoText) geo).isSerifFont() : false;
	}

	/**
	 * @param g2 graphics
	 */
	protected final void drawMultilineText(GGraphics2D g2) {

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
					GPoint p = EuclidianStatic.drawIndexedString(
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
			GPoint p = EuclidianStatic
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
	 * @param x mouse x-coord
	 * @param y mouse y-coord
	 * @return true if hit
	 */
	public boolean hitLabel(int x, int y) {
		return labelRectangle.contains(x, y);
	}

	private boolean forcedLineType;

	private HatchingHandler hatchingHandler;

	/**
	 * Set fixed line type and ignore line type of the geo. Needed for
	 * inequalities.
	 * 
	 * @param type line type
	 */
	public final void forceLineType(int type) {
		forcedLineType = true;
		lineType = type;
	}

	/**
	 * Update strokes (default,selection,deco) accordingly to geo
	 * 
	 * @param fromGeo geo whose style should be used for the update
	 */
	final public void updateStrokes(GeoElementND fromGeo) {
		strokedShape = null;
		strokedShape2 = null;

		if (lineThickness != fromGeo.getLineThickness()) {
			lineThickness = fromGeo.getLineThickness();
			if (!forcedLineType)
				lineType = fromGeo.getLineType();

			float width = lineThickness / 2.0f;
			objStroke = EuclidianStatic.getStroke(width, lineType);
			decoStroke = EuclidianStatic.getStroke(width,
					EuclidianStyleConstants.LINE_TYPE_FULL);
			selStroke = EuclidianStatic.getStroke(width
					+ EuclidianStyleConstants.SELECTION_ADD,
					EuclidianStyleConstants.LINE_TYPE_FULL);
		} else if (lineType != fromGeo.getLineType()) {
			if (!forcedLineType)
				lineType = fromGeo.getLineType();

			float width = lineThickness / 2.0f;
			objStroke = EuclidianStatic.getStroke(width, lineType);
		}
	}
	/**
	 * Update strokes (default,selection,deco) accordingly to geo;
	 * ignores line style
	 * 
	 * @param fromGeo geo whose style should be used for the update
	 */
	public final void updateStrokesJustLineThickness(GeoElement fromGeo) {
		strokedShape = null;
		strokedShape2 = null;

		if (lineThickness != fromGeo.lineThickness) {
			lineThickness = fromGeo.lineThickness;

			float width = lineThickness / 2.0f;
			objStroke = geogebra.common.factories.AwtFactory.prototype.newBasicStroke(width, objStroke.getEndCap(), objStroke.getLineJoin(), objStroke.getMiterLimit(),
					objStroke.getDashArray(), 0.0f);
			decoStroke = geogebra.common.factories.AwtFactory.prototype.newBasicStroke(width, objStroke.getEndCap(), objStroke.getLineJoin(), objStroke.getMiterLimit(),
					decoStroke.getDashArray(), 0.0f);
			selStroke = geogebra.common.factories.AwtFactory.prototype.newBasicStroke(width + EuclidianStyleConstants.SELECTION_ADD, objStroke.getEndCap(), objStroke.getLineJoin(), objStroke.getMiterLimit(),
					selStroke.getDashArray(), 0.0f);

		} 
	}


	


	/**
	 * Fills given shape
	 * @param g2 graphics
	 * @param fillShape shape to be filled
	 * @param usePureStroke true to use pure stroke
	 */
	protected void fill(GGraphics2D g2, GShape fillShape, boolean usePureStroke) {
		if (isForceNoFill())
			return;
		if (geo.isHatchingEnabled()) {
			// use decoStroke as it is always full (not dashed/dotted etc)
			getHatchingHandler().setHatching(g2, decoStroke,
					geo.getObjectColor(),
					geo.getBackgroundColor(),
					geo.getAlphaValue(), geo.getHatchingDistance(),
					geo.getHatchingAngle(),
					geo.getFillType(),
					geo.getFillSymbol(),
					geo.getKernel().getApplication());
			if (usePureStroke)
				EuclidianStatic.fillWithValueStrokePure(fillShape, g2);
			else
				g2.fill(fillShape);

		} else if (geo.getFillType() == GeoElement.FillType.IMAGE) {
			getHatchingHandler().setTexture(g2, geo, geo.getAlphaValue());
			g2.fill(fillShape);
		} else if (geo.getAlphaValue() > 0.0f) {
			g2.setPaint(geo.getFillColor());
			
			
			// needed in web when we are using the dashed lines hack, eg x < 3
			// otherwise this doesn't work in web.awt.Graphics2D
			//		doDrawShape(shape);
			//      context.fill();		
			if (lineType != EuclidianStyleConstants.LINE_TYPE_FULL && geo.getKernel().getApplication().isHTML5Applet()) {
				g2.setStroke(EuclidianStatic.getStroke(0, EuclidianStyleConstants.LINE_TYPE_FULL));
			}
			
			
			g2.fill(fillShape);
		}

	}
	
	private HatchingHandler getHatchingHandler() {
		if (hatchingHandler == null) {
			hatchingHandler = new HatchingHandler();
		}
		
		return hatchingHandler;
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
	public void setShape(GArea shape) {
		this.shape = shape;
	}

	/**
	 * @return the shape
	 */
	public GArea getShape() {
		return shape;
	}

	/**
	 * @return true if trace is on
	 */
	public boolean isTracing() {
		return isTracing;
	}
	/** draw trace of this geo into given Graphics2D 
	 * @param g2 graphics*/
	protected void drawTrace(GGraphics2D g2) {
		// do nothing, overridden where needed
	}
	
	/**
	 * @return whether the to-be-drawn geoElement is filled, meaning the
	 *         alpha-value is greater zero, or hatching is enabled.
	 */
	public boolean isFilled(){
		return (geo.getAlphaValue() > 0.0f || geo.isHatchingEnabled());
	}
}
