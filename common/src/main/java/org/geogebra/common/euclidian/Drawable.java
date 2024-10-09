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

package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.draw.DrawDynamicCaption;
import org.geogebra.common.euclidian.draw.DrawText;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.EdgeInsets;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.StringUtil;

import com.google.j2objc.annotations.Weak;

/**
 * 
 * @author Markus
 */
public abstract class Drawable extends DrawableND {

	public static final double UI_ELEMENT_HIGHLIGHT_WIDTH = 5;
	protected static final int HIGHLIGHT_OFFSET = 4;
	private static final int HIGHLIGHT_DIAMETER = 8;
	public static final int LATEX_Y_LABEL_OFFSET = 10;

	private boolean forceNoFill;

	/**
	 * Default stroke for this drawable
	 */
	protected GBasicStroke objStroke = EuclidianStatic.getDefaultStroke();
	/**
	 * Stroke for this drawable in case referenced geo is selected
	 */
	protected GBasicStroke selStroke = EuclidianStatic
			.getDefaultSelectionStroke();
	/**
	 * Stroke for decorations; always full
	 */
	protected GBasicStroke decoStroke = EuclidianStatic.getDefaultStroke();

	private int lineThickness = -1;
	private int lineType = -1;

	/**
	 * View in which this is drawn
	 */
	@Weak
	protected EuclidianView view;

	/**
	 * Referenced GeoElement
	 */
	protected GeoElement geo;
	/** x-coord of the label */
	public int xLabel;
	/** y-coord of the label */
	public int yLabel;
	/** label Description */
	public String labelDesc;
	private String oldLabelDesc;
	private boolean labelHasIndex = false;
	/** for label hit testing */
	protected GRectangle labelRectangle = AwtFactory.getPrototype()
			.newRectangle(0, 0);
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
	private boolean forcedLineType;

	private HatchingHandler hatchingHandler;

	private GRectangle tempFrame;

	/**
	 * Whether current paint is the first one
	 */
	protected boolean firstCall = true;
	private GeoElement geoForLabel;
	private DrawDynamicCaption drawDynamicCaption;
	private int labelMargin = DrawText.DEFAULT_MARGIN;

	/**
	 * Create a default drawable. GeoElement and the view must be set
	 * after creation in the constructor.
	 */
	public Drawable() {
		this(null, null);
	}

	/**
	 * Create a drawable.
	 *
	 * @param view euclidean view
	 * @param geo geo element
	 */
	public Drawable(EuclidianView view, GeoElement geo) {
		this.view = view;
		this.geo = geo;
	}

	public void initDynamicCaption() {
		drawDynamicCaption = new DrawDynamicCaption(view, this);
	}

	public DrawDynamicCaption getDynamicCaption() {
		return drawDynamicCaption;
	}

	@Override
	public abstract void update();

	/**
	 * Draws this drawable to given graphics
	 * 
	 * @param g2
	 *            graphics
	 */
	public abstract void draw(GGraphics2D g2);

	/**
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 * @param hitThreshold
	 *            pixel threshold
	 * @return true if hit
	 */
	public abstract boolean hit(int x, int y, int hitThreshold);

	/**
	 * @param rect
	 *            rectangle
	 * @return true if the whole drawable is inside
	 */
	public abstract boolean isInside(GRectangle rect);

	/**
	 * @param rect
	 *            rectangle
	 * @return true if a part of this Drawable is within the rectangle
	 */
	public boolean intersectsRectangle(GRectangle rect) {
		GArea s = getShape();
		if (s == null) {
			return false;
		}
		if (geo.isFilled()) {
			return s.intersects(rect);
		}
		return s.intersects(rect) && !s.contains(rect);
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	/**
	 * @return bounding box with handlers
	 */
	public BoundingBox<? extends GShape> getBoundingBox() {
		return null;
	}

	@Override
	public double getLabelX() {
		return xLabel;
	}

	@Override
	public double getLabelY() {
		return yLabel;
	}

	/**
	 * Updates font size
	 */
	public void updateFontSize() {
		// do nothing, overriden in drawables
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 * 
	 * @return null when this Drawable is infinite or undefined
	 */
	public @CheckForNull GRectangle getBounds() {
		return null;
	}

	/**
	 * Draws label of referenced geo
	 * 
	 * @param g2
	 *            graphics
	 */
	public final void drawLabel(GGraphics2D g2) {
		if (getDynamicCaption() != null && getDynamicCaption().isEnabled()) {
			getDynamicCaption().draw(g2);
			return;
		}
		if (labelDesc == null) {
			return;
		}
		String label = labelDesc;

		// stripping off helper syntax from captions
		// assuming that non-caption labels will not contain
		// that helper syntax anyway
		int ind = label.indexOf("%style=");
		if (ind > -1) {
			label = label.substring(0, ind);
		}

		GFont oldFont = null;

		// allow LaTeX caption surrounded by $ $
		if (label.length() > 1 && (label.charAt(0) == '$')
				&& label.endsWith("$")) {
			boolean serif = true; // nice "x"s
			if (geo.isGeoText()) {
				serif = ((GeoText) geo).isSerifFont();
			}
			int offsetY = view.getFontSize(); // make sure LaTeX labels
													// don't go
													// off bottom of screen
			App app = view.getApplication();
			GDimension dim = app.getDrawEquation().drawEquation(
					geo.getKernel().getApplication(), geo, g2, xLabel,
					yLabel - offsetY, label.substring(1, label.length() - 1),
					g2.getFont(), serif, g2.getColor(), g2.getBackground(),
					true, false, view.getCallBack(geo, firstCall));
			firstCall = false;
			labelRectangle.setBounds(xLabel, yLabel - offsetY, dim.getWidth(),
					dim.getHeight());
			return;
		}

		// label changed: check for bold or italic tags in caption
		if (!labelDesc.equals(oldLabelDesc)
				|| (labelDesc.length() > 0 && labelDesc.charAt(0) == '<')) {
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

				g2.setFont(g2.getFont()
						.deriveFont(GFont.BOLD + (italic ? GFont.ITALIC : 0)));
				label = label.substring(3, label.length() - 4);
			}
		}

		// no index in label: draw it fast
		int fontSize = g2.getFont().getSize();
		if (labelDesc.equals(oldLabelDesc) && !labelHasIndex
				&& lastFontSize == fontSize) {
			view.drawStringWithOutline(g2, label, xLabel, yLabel,
					geo.getObjectColor());
			labelRectangle.setLocation(xLabel, yLabel - fontSize);
		} else { // label with index or label has changed:
					// do the slower index drawing routine and check for indices
			oldLabelDesc = labelDesc;

			GPoint p = EuclidianStatic.drawIndexedString(view.getApplication(),
					g2, label, xLabel, yLabel, isSerif(), view,
					geo.getObjectColor());
			labelHasIndex = p.y > 0;
			labelRectangle.setBounds(xLabel, yLabel - fontSize, p.x,
					fontSize + p.y);
			lastFontSize = fontSize;
		}

		if (oldFont != null) {
			g2.setFont(oldFont);
		}
	}

	/**
	 * Adapts xLabel and yLabel to make sure that the label rectangle fits fully
	 * on screen.
	 * 
	 * @param Xmultiplier
	 *            multiply the x size by it to ensure fitting (default: 1.0)
	 * @param Ymultiplier
	 *            multiply the y size by it to ensure fitting (default: 1.0)
	 */
	private void ensureLabelDrawsOnScreen(double Xmultiplier,
			double Ymultiplier, GFont font) {
		// draw label and
		int widthEstimate = (int) labelRectangle.getWidth();
		int heightEstimate = (int) labelRectangle.getHeight();
		int screenWidth = view.getWidth();
		int screenHeight = view.getHeight();
		boolean roughEstimate = false;

		if (!labelDesc.equals(oldLabelDesc) || lastFontSize != font.getSize()) {
			if (isLabelLatex()) {
				// for LaTeX we need proper repaint
				drawLabel(view.getTempGraphics2D(font));
				widthEstimate = (int) labelRectangle.getWidth();
				heightEstimate = (int) labelRectangle.getHeight();
				yLabel -= LATEX_Y_LABEL_OFFSET;
			} else {
				// if we use name = value, this may still be called pretty
				// often.
				// Hence use heuristic here instead of measurement
				heightEstimate = (int) (StringUtil.getPrototype()
						.estimateHeight(labelDesc, font) * Ymultiplier);

				widthEstimate = (int) (StringUtil.getPrototype()
						.estimateLengthHTML(labelDesc, font) * Xmultiplier);
				roughEstimate = true;
			}
		}
		// make sure labelRectangle fits on screen horizontally
		if (xLabel < 3) {
			xLabel = 3;
		} else if (xLabel > screenWidth - widthEstimate - 3) {
			if (roughEstimate) {
				drawLabel(view.getTempGraphics2D(font));
				widthEstimate = (int) labelRectangle.getWidth();
				heightEstimate = (int) labelRectangle.getHeight();
				roughEstimate = false;
			}
			xLabel = Math.min(xLabel, screenWidth - widthEstimate - 3);
		}

		if (yLabel < heightEstimate) {
			if (roughEstimate) {
				drawLabel(view.getTempGraphics2D(font));
				heightEstimate = (int) labelRectangle.getHeight();
			}
			yLabel = Math.max(yLabel, heightEstimate);
		} else {
			yLabel = Math.min(yLabel, screenHeight);
		}
		// Fit label in safe area
		EdgeInsets insets = view.getSafeAreaInsets();
		if (widthEstimate > screenWidth - insets.getLeft() - insets.getRight()) {
			xLabel = (screenWidth - widthEstimate) / 2;
		} else if (xLabel < insets.getLeft()) {
			xLabel = insets.getLeft();
		} else if (xLabel + widthEstimate > screenWidth - insets.getRight()) {
			xLabel = screenWidth - insets.getRight() - widthEstimate;
		}
		if (yLabel - heightEstimate < insets.getTop()) {
			if (roughEstimate) {
				drawLabel(view.getTempGraphics2D(font));
				heightEstimate = (int) labelRectangle.getHeight();
			}
			yLabel = insets.getTop() + heightEstimate;
		} else if (yLabel > screenHeight - insets.getBottom()) {
			yLabel = screenHeight - insets.getBottom();
		}
		// update label rectangle position
		labelRectangle.setLocation(xLabel, yLabel - view.getFontSize());
	}

	private boolean isLabelLatex() {
		return labelDesc.startsWith("$");
	}

	/**
	 * @param g2
	 *            graphics
	 * @param font
	 *            font
	 * @param fgColor
	 *            text color
	 * @param bgColor
	 *            background color
	 */
	public final void drawMultilineLaTeX(GGraphics2D g2, GFont font,
			GColor fgColor, GColor bgColor) {
		if (labelDesc != null) {
			EuclidianStatic.drawMultilineLaTeX(view.getApplication(),
					view.getTempGraphics2D(font), geo, g2, font, fgColor,
					bgColor, labelDesc, xLabel, yLabel, isSerif(),
					view.getCallBack(geo, firstCall),
					labelRectangle);
			firstCall = false;
		}
		// else: undefined text, do nothing
	}

	/**
	 * @return true if serif font is used for GeoText
	 */
	final boolean isSerif() {
		return geo.isGeoText() && ((GeoText) geo).isSerifFont();
	}

	/**
	 * @param g2
	 *            graphics
	 * @param textFont
	 *            font
	 */
	protected final void drawMultilineText(GGraphics2D g2, GFont textFont) {
		if (labelDesc == null) {
			return;
		}

		// no index in text
		if (labelDesc.equals(oldLabelDesc) && !labelHasIndex) {

			// sets labelRectangle
			EuclidianStatic.drawMultiLineText(
					view.getApplication(), labelDesc, xLabel, yLabel, g2,
					isSerif(), textFont, labelRectangle, geo, labelMargin);
		} else {
			// text with indices
			// label description has changed, search for possible indices
			oldLabelDesc = labelDesc;

			labelHasIndex = EuclidianStatic
					.drawIndexedMultilineString(view.getApplication(),
							labelDesc, g2, labelRectangle, textFont, isSerif(),
							xLabel, yLabel, labelMargin);
		}
	}

	/**
	 * Adds geo's label offset to xLabel and yLabel.
	 * 
	 * @return whether the label fits on screen
	 */
	final protected boolean addLabelOffset() {
		if (geo.labelOffsetX == 0 && geo.labelOffsetY == 0) {
			return false;
		}

		int x = xLabel + geo.labelOffsetX;
		int y = yLabel + geo.labelOffsetY;

		// don't let offset move label out of screen
		int xmax = view.getWidth() - 15;
		int ymax = view.getHeight() - 5;
		if (x < 5 || x > xmax) {
			return false;
		}
		if (y < 15 || y > ymax) {
			return false;
		}

		xLabel = x;
		yLabel = y;
		if (isLabelLatex()) {
			yLabel -= LATEX_Y_LABEL_OFFSET;
		}
		return true;
	}

	/**
	 * Adds geo's label offset to xLabel and yLabel.
	 * 
	 * @param font
	 *            used font
	 * 
	 */
	public final void addLabelOffsetEnsureOnScreen(GFont font) {
		addLabelOffsetEnsureOnScreen(1.0, 1.0, font);
	}

	/**
	 * Adds geo's label offset to xLabel and yLabel.
	 * 
	 * @param Xmultiplier
	 *            multiply the x size by it to ensure fitting
	 * @param Ymultiplier
	 *            multiply the y size by it to ensure fitting
	 * @param font
	 *            font
	 * 
	 */
	public final void addLabelOffsetEnsureOnScreen(double Xmultiplier,
			double Ymultiplier, GFont font) {
		// MAKE SURE LABEL STAYS ON SCREEN
		xLabel += geo.labelOffsetX;
		yLabel += geo.labelOffsetY;

		// change xLabel and yLabel so that label stays on screen
		ensureLabelDrawsOnScreen(Xmultiplier, Ymultiplier, font);
	}

	/**
	 * Was the label clicked at? (mouse pointer location (x,y) in screen coords)
	 * 
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 * @return true if hit
	 */
	public boolean hitLabel(int x, int y) {
		return labelRectangle.contains(x, y);
	}

	/**
	 * Was clicked at the handlers of bounding box? (mouse pointer location
	 * (x,y) in screen coords)
	 * 
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 * @param hitThreshold
	 *            - threshold
	 * @return bounding box handler
	 */
	public @Nonnull EuclidianBoundingBoxHandler hitBoundingBoxHandler(int x, int y, int hitThreshold) {
		if (getBoundingBox() != null && getBoundingBox() == view.getBoundingBox()) {
			return getBoundingBox().getHitHandler(x, y, hitThreshold);
		}

		return EuclidianBoundingBoxHandler.UNDEFINED;
	}

	/**
	 * Set fixed line type and ignore line type of the geo. Needed for
	 * inequalities.
	 * 
	 * @param type
	 *            line type
	 */
	public final void forceLineType(int type) {
		forcedLineType = true;
		lineType = type;
	}

	/**
	 * Update strokes (default,selection,deco) accordingly to geo
	 * 
	 * @param fromGeo
	 *            geo whose style should be used for the update
	 */
	final public void updateStrokes(GeoElementND fromGeo) {
		updateStrokes(fromGeo, 0);
	}

	/**
	 * Update strokes (default,selection,deco) accordingly to geo
	 * 
	 * @param fromGeo
	 *            geo whose style should be used for the update
	 * @param minThickness
	 *            minimal acceptable thickness
	 */
	final public void updateStrokes(GeoElementND fromGeo, int minThickness) {
		strokedShape = null;
		strokedShape2 = null;

		if (lineThickness != fromGeo.getLineThickness()) {
			lineThickness = Math.max(minThickness, fromGeo.getLineThickness());
			if (!forcedLineType) {
				lineType = fromGeo.getLineType();
			}

			double width = lineThickness / 2.0;
			objStroke = EuclidianStatic.getStroke(width, lineType);
			decoStroke = EuclidianStatic.getStroke(width,
					EuclidianStyleConstants.LINE_TYPE_FULL);

			selStroke = EuclidianStatic.getStroke(
					!isShape(fromGeo) ? 2 * Math.max(width, 1) + 2
									: width + EuclidianStyleConstants.SELECTION_ADD,
					EuclidianStyleConstants.LINE_TYPE_FULL);
		} else if (lineType != fromGeo.getLineType()) {
			if (!forcedLineType) {
				lineType = fromGeo.getLineType();
			}

			double width = lineThickness / 2.0;
			objStroke = EuclidianStatic.getStroke(width, lineType);
		}
	}

	protected boolean isShape(GeoElementND fromGeo) {
		return view.getApplication().isWhiteboardActive()
				&& (fromGeo.isGeoPolygon() || fromGeo.isGeoSegment() || fromGeo.isGeoConic());
	}

	/**
	 * Update strokes (default,selection,deco) accordingly to geo; ignores line
	 * style
	 * 
	 * @param fromGeo
	 *            geo whose style should be used for the update
	 */
	public final void updateStrokesJustLineThickness(GeoElement fromGeo) {
		strokedShape = null;
		strokedShape2 = null;

		if (lineThickness != fromGeo.getLineThickness()) {
			lineThickness = fromGeo.getLineThickness();

			double width = lineThickness / 2.0;
			objStroke = AwtFactory.getPrototype().newBasicStroke(width,
					objStroke.getEndCap(), objStroke.getLineJoin(),
					objStroke.getMiterLimit(), objStroke.getDashArray());
			decoStroke = AwtFactory.getPrototype().newBasicStroke(width,
					objStroke.getEndCap(), objStroke.getLineJoin(),
					objStroke.getMiterLimit(), decoStroke.getDashArray());
			selStroke = AwtFactory.getPrototype().newBasicStroke(
					2 * width + 2,
					objStroke.getEndCap(), objStroke.getLineJoin(),
					objStroke.getMiterLimit(), selStroke.getDashArray());

		}
	}

	/**
	 * Fills given shape
	 * 
	 * @param g2
	 *            graphics
	 * @param fillShape
	 *            shape to be filled
	 */
	public void fill(GGraphics2D g2, GShape fillShape) {
		if (isForceNoFill()) {
			return;
		}
		if (geo.getFillType() != FillType.STANDARD) {
			fillWithHatchOrImage(g2, fillShape, geo.getObjectColor());
		} else if (geo.getAlphaValue() > 0.0f) {
			g2.setPaint(geo.getFillColor());
			g2.fill(fillShape);
		}
	}

	/**
	 * Fills given shape
	 *
	 * @param g2
	 *            graphics
	 * @param fillShape
	 *            shape to be filled
	 * @param color hatching color
	 */
	public void fillWithHatchOrImage(GGraphics2D g2, GShape fillShape, GColor color) {
		if (geo.getFillType() == FillType.IMAGE && geo.getFillImage() != null) {
			getHatchingHandler().setTexture(g2, geo, geo.getAlphaValue());
			g2.fill(fillShape);
			return;
		}
		// use decoStroke as it is always full (not dashed/dotted etc)
		GPaint gpaint = getHatchingHandler().setHatching(g2, decoStroke,
				color, geo.getBackgroundColor(),
				geo.getAlphaValue(), geo.getHatchingDistance(),
				geo.getHatchingAngle(), geo.getFillType(),
				geo.getFillSymbol(), geo.getKernel().getApplication());

		g2.setPaint(gpaint);
		getHatchingHandler().fill(g2, fillShape, getView().getApplication());

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
	public final void setShape(GArea shape) {
		this.shape = shape;
	}

	/**
	 * @return the shape
	 */
	public GArea getShape() {
		return shape;
	}

	@Override
	public boolean isTracing() {
		return isTracing;
	}

	/**
	 * draw trace of this geo into given Graphics2D
	 * 
	 * @param g2
	 *            graphics
	 */
	protected void drawTrace(GGraphics2D g2) {
		// do nothing, overridden where needed
	}

	/**
	 * @return view in which this is drawn
	 */
	public EuclidianView getView() {
		return view;
	}

	/**
	 * @return whether geo can be visible in any euclidian view
	 */
	public final boolean isEuclidianVisible() {
		return geo.isEuclidianVisible();
	}

	/**
	 * @return If the {@code GeoElement} has line opacity then a {@code GColor}
	 *         object with the alpha value set, else the original {@code GColor}
	 *         .
	 */
	protected GColor getObjectColor() {
		GColor color = geo.getObjectColor();
		if (geo.hasLineOpacity()) {
			color = color.deriveWithAlpha(geo.getLineOpacity());
		}
		return color;
	}

	/**
	 * Update when view was changed and geo is still the same
	 */
	public void updateForView() {
		update();
	}

	/**
	 * method to handle corner or side drag of bounding box to resize geo
	 * 
	 * @param point
	 *            - mouse drag event
	 * @param handler
	 *            - which corner was dragged
	 */
	public void updateByBoundingBoxResize(GPoint2D point,
			EuclidianBoundingBoxHandler handler) {
		// do nothing here
	}

	/**
	 * @param text
	 *            text
	 * @param font
	 *            font
	 * @param g2
	 *            graphics
	 * @return width of text with given font
	 */
	public int measureTextWidth(String text, GFont font, GGraphics2D g2) {
		GTextLayout layout = getTextLayout(text, font, g2);
		return layout != null ? (int) layout.getAdvance() : 0;
	}

	/**
	 * @param text
	 *            text
	 * @param font
	 *            font
	 * @param g2
	 *            graphics
	 * @return text layout
	 */
	public static @CheckForNull GTextLayout getTextLayout(String text, GFont font, GGraphics2D g2) {
		if (text == null || text.isEmpty()) {
			return null;
		}
		return AwtFactory.getPrototype().newTextLayout(text, font,
				g2.getFontRenderContext());
	}

	/**
	 * @return bounds of the drawn path
	 */
	@Override
	public @CheckForNull GRectangle2D getBoundsForStylebarPosition() {
		return getBounds();
	}

	/**
	 * @return geo that determines labeling and highlighting
	 */
	public GeoElement getTopLevelGeo() {
		return geoForLabel == null ? getGeoElement() : geoForLabel;
	}

	/**
	 * @param geo
	 *            that determines labeling and highlighting
	 */
	public void setTopLevelGeo(GeoElement geo) {
		geoForLabel = geo;
	}

	/**
	 * @param x
	 *            x-coord of top left
	 * @param y
	 *            y-coord of top left
	 * @param w
	 *            width
	 * @param h
	 *            height
	 * @return frame for getBounds
	 */
	protected GRectangle getTempFrame(int x, int y, int w, int h) {
		if (tempFrame == null) {
			tempFrame = AwtFactory.getPrototype().newRectangle(x, y, w, h);
		} else {
			tempFrame.setBounds(x, y, w, h);
		}

		return tempFrame;
	}

	/**
	 * @param handler
	 *            bounding box handler
	 * @return true if 'handler' is a corner handler.
	 */
	protected static boolean isCornerHandler(
			EuclidianBoundingBoxHandler handler) {
		return handler == EuclidianBoundingBoxHandler.BOTTOM_LEFT
				|| handler == EuclidianBoundingBoxHandler.BOTTOM_RIGHT
				|| handler == EuclidianBoundingBoxHandler.TOP_LEFT
				|| handler == EuclidianBoundingBoxHandler.TOP_RIGHT;
	}

	@Override
	public DrawableND createDrawableND(GeoElement subGeo) {
		return view.newDrawable(subGeo);
	}

	/**
	 * @return whether the drawable should be highlighted
	 */
	public boolean isHighlighted() {
		return getTopLevelGeo().doHighlighting();
	}

	@Override
	public void setPartialHitClip(GRectangle rect) {
		// just strokes
	}

	@Override
	public GRectangle getPartialHitClip() {
		return null;
	}

	/**
	 * @return bounding box for multi-selection, possibly clipped
	 */
	public GRectangle2D getBoundsClipped() {
		return getBoundingBox() != null ? getBoundingBox().getRectangle() : getBounds();
	}

	/**
	 * Reset partial hit rectangle after click
	 * 
	 * @param x
	 *            screen x-coord of the click
	 * @param y
	 *            screen y-coord of the click
	 * @return whether we clicked outside of partial hit
	 */
	public boolean resetPartialHitClip(int x, int y) {
		return false; // only for strokes
	}

	/**
	 * @param points
	 *            list of points defining the drawable
	 */
	public void fromPoints(ArrayList<GPoint2D> points) {
		// only shapes
	}

	/**
	 * @return list of points defining the drawable
	 */
	protected List<GPoint2D> toPoints() {
		GRectangle2D bounds = getBoundingBox() != null
				? getBoundingBox().getRectangle()
				: getBounds();
		if (bounds == null) {
			return Collections.emptyList();
		}
		List<GPoint2D> ret = new ArrayList<>(2);
		ret.add(new MyPoint(bounds.getMinX(), bounds.getMinY()));
		ret.add(new MyPoint(bounds.getMaxX(), bounds.getMaxY()));
		return ret;
	}

	/**
	 * Update this if it's marked as outdated
	 */
	public void updateIfNeeded() {
		if (needsUpdate()) {
			setNeedsUpdate(false);
			update();
		}
	}

	public BoundingBox<? extends GShape> getSelectionBoundingBox() {
		return new SingleBoundingBox(view.getApplication().getPrimaryColor());
	}

	public GBasicStroke getDecoStroke() {
		return decoStroke;
	}

	public int getCaptionY(boolean laTeX, int captionHeight) {
		return yLabel;
	}

	/**
	 * Label margin is used for multiline plain text rendering
	 * (space between actual text and classic bounding box)
	 * For consistency with normal captions set to 0 for dynamic captions.
	 * @param labelMargin label margin
	 */
	public void setLabelMargin(int labelMargin) {
		this.labelMargin = labelMargin;
	}

	/**
	 * @return whether the drawable is interactive
	 */
	public boolean isInteractiveEditor() {
		return false;
	}

	protected void drawHighlightRect(GGraphics2D g2) {
		g2.drawRoundRect((int) labelRectangle.getX() - HIGHLIGHT_OFFSET / 2,
				(int) labelRectangle.getY() - HIGHLIGHT_OFFSET / 2,
				(int) labelRectangle.getWidth() + HIGHLIGHT_OFFSET,
				(int) labelRectangle.getHeight() + HIGHLIGHT_OFFSET,
				HIGHLIGHT_DIAMETER, HIGHLIGHT_DIAMETER);
	}

	protected void drawAndUpdateTraceIfNeeded(boolean showTrace) {
		isTracing = showTrace;
		if (showTrace) {
			view.drawTrace(this);
		}
	}

	/**
	 * Draw this to graphics if this is a stroke shape, do nothing otherwise
	 * @param graphics target graphics
	 */
	public void drawStroke(GGraphics2D graphics) {
		// implemented for segments and loci only
	}
}
