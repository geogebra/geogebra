/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.euclidian.draw.dropdown;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.CanvasDrawable;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.util.DrawSelectedItem;
import org.geogebra.common.gui.util.DropDownList;
import org.geogebra.common.gui.util.DropDownListener;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Draw a GeoList containing drawable objects
 * 
 * @author Markus Hohenwarter
 */
public final class DrawDropDownList extends CanvasDrawable
		implements DropDownListener, MoveSelector {
	private static final int LABEL_COMBO_GAP = 10;
	private static final int COMBO_CONTEXT_GAP = 2;
	public static final int COMBO_TEXT_MARGIN = 5;
	private final DrawSelectedItem drawSelected;
	private final OptionScroller scroller;
	private final DropDownModel model;

	/** corresponding list as geo */
	GeoList geoList;
	/** whether this is visible */
	boolean isVisible;
	/** dropdown */
	DropDownList dropDown;
	/** selected text */
	String selectedText;

	private GDimension selectedDimension;
	private boolean latexLabel;

	private final DrawOptions drawOptions;
	private boolean seLatex;

	/**
	 * Creates new drawable list
	 * 
	 * @param view
	 *            view
	 * @param geoList
	 *            list
	 */
	public DrawDropDownList(EuclidianView view, GeoList geoList) {
		this.view = view;
		this.geoList = geoList;
		geo = geoList;

		dropDown = new DropDownList(view.getApplication(), this);
		scroller = new OptionScroller(dropDown);
		model = new DropDownModel(getLabelFont(), geoList);
		drawOptions = new DrawOptions(this, model, view,
				scroller);

		drawSelected = new DrawSelectedItem();
		update();
	}

	private String getLabelText() {
		// don't need to worry about labeling options, just check if caption
		// set or not

		if (!"".equals(geo.getRawCaption())) {
			return geo.getCaption(StringTemplate.defaultTemplate);
		}

		// make sure there's something to drag
		return Unicode.NBSP + "" + Unicode.NBSP + "" + Unicode.NBSP;
	}

	@Override
	public void update() {
		isVisible = geo.isEuclidianVisible() && geoList.size() != 0;
		int fontSize = (int) (view.getFontSize()
				* geoList.getFontSizeMultiplier());
		setLabelFontSize(fontSize);
		if (!geo.isSelectionAllowed(view)) {
			setOptionsVisible(false);
		}
		if (!isVisible) {
			return;
		}

		// eg: size changed etc
		labelDesc = getLabelText();

		xLabel = geoList.getAbsoluteScreenLocX();
		yLabel = geoList.getAbsoluteScreenLocY();
		if (getDynamicCaption() != null && getDynamicCaption().isEnabled()) {
			getDynamicCaption().update();
		}
		labelRectangle.setBounds(xLabel, yLabel,
				(int) (getHitRect().getWidth()),
				(int) (getHitRect().getHeight()));
		geoList.setTotalWidth(getTotalWidth());
		geoList.setTotalHeight(getTotalHeight());
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (isVisible) {
			setLabelFont("");
			drawOnCanvas(g2);
		}
	}

	/**
	 * Returns whether any one of the list items is at the given screen
	 * position.
	 */
	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		DrawDropDownList opened = view.getOpenedComboBox();
		if (opened != null && opened != this && opened.isOptionsHit(x, y)) {
			return false;
		}

		return super.hit(x, y, hitThreshold) || drawSelected.isOpenButtonHit(x, y)
				|| isOptionsHit(x, y);
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	public GRectangle getBounds() {
		if (getHitRect() == null) {
			return null;
		}

		return getHitRect().getBounds();
	}

	@Override
	protected void drawWidget(GGraphics2D g2) {
		updateMetrics(g2);
		String labelText = getLabelText();
		int textLeft = boxLeft + COMBO_TEXT_MARGIN;
		GColor bgColor = geo.getBackgroundColor() != null
				? geo.getBackgroundColor() : GColor.WHITE;

		drawSelected.drawBounds(geoList, g2, bgColor, boxLeft, boxTop, boxWidth,
				boxHeight);

		if (!geoList.hasScreenLocation() && boxWidth != 0) {
			initScreenLocation();
		}

		g2.setPaint(GColor.LIGHT_GRAY);
		highlightLabel(g2, latexLabel);
		g2.setPaint(geo.getObjectColor());

		// Draw the selected line
		int textBottom;
		if (seLatex) {
			textBottom = boxTop
					+ (boxHeight - selectedDimension.getHeight()) / 2;
		} else {
			textBottom = alignTextToBottom(g2, boxTop, boxHeight, selectedText);
		}

		drawSelectedText(g2, textLeft, textBottom, true);
		drawSelected.drawOpenControl(g2, boxLeft, boxTop, boxWidth, boxHeight);

		if (geo.isLabelVisible()) {
			drawLabel(g2, geoList, labelText);
		}

		drawOptions.draw(g2, boxLeft, boxTop + boxHeight + COMBO_CONTEXT_GAP);
	}

	private void initScreenLocation() {
		geoList.setScreenLocation(Math.min(xLabel, boxLeft),
				Math.min(yLabel, boxTop));
	}

	private int alignTextToBottom(GGraphics2D g2, int top, int height,
			String text) {
		int base = (height + getTextDescent(g2, text)) / 2;
		return top + base + (height - base) / 2;
	}

	private int getTextDescent(GGraphics2D g2, String text) {
		// make sure layout won't be null ("" makes it null).

		GTextLayout layout = getLayout(g2, text, getLabelFont());
		return (int) layout.getDescent();
	}

	private void drawLabel(GGraphics2D g2, GeoElement geo0, String text) {
		if (getDynamicCaption() != null && getDynamicCaption().isEnabled()) {
			getDynamicCaption().draw(g2);
		} else {
			boolean latex = isLatexString(text);
			if (latex) {
				drawLatex(g2, geo0, getLabelFont(), text, xLabel,
						getCaptionY(true, labelSize.y));
			} else {
				int textBottom = getCaptionY(false, labelSize.y);
				g2.setPaint(geo.getObjectColor());
				g2.setFont(getLabelFont());
				EuclidianStatic.drawIndexedString(view.getApplication(), g2, text,
						xLabel, textBottom, false);
			}
		}
	}

	@Override
	public int getCaptionY(boolean latex, int height) {
		return latex ? boxTop + (boxHeight - height) / 2
				: boxTop + (boxHeight + getLabelFontSize() - COMBO_TEXT_MARGIN) / 2;
	}

	@Override
	protected void highlightLabel(GGraphics2D g2, boolean latex) {
		if (geo.isLabelVisible() && isHighlighted() && latex
				&& !geo.hasDynamicCaption()) {
			g2.fillRect(xLabel, boxTop + (boxHeight - labelSize.y) / 2,
					labelSize.x, labelSize.y);
		} else {
			super.highlightLabel(g2, latex);
		}
	}

	@Override
	protected int getLabelGap() {
		return LABEL_COMBO_GAP;
	}

	private void updateMetrics(GGraphics2D g2) {
		drawOptions.onResize(view.getWidth(), view.getHeight());

		GeoElement geoItem = geoList.getSelectedElement();
		if (geoItem != null && GeoList.needsLatex(geoItem)) {
			selectedText = geoItem.toLaTeXString(false,
					StringTemplate.latexTemplate);
			seLatex = true;
		} else {
			// realTemplate: make sure Sequence((t,t),t,1,5) works
			selectedText = geoList.getItemDisplayString(geoItem,
					StringTemplate.realTemplate);
			seLatex = isLatexString(selectedText);
		}

		selectedDimension = drawSelectedText(g2, 0, 0, false);
		latexLabel = measureLabel(g2, geoList, getLabelText());
		labelRectangle.setBounds(boxLeft - 1, boxTop - 1, boxWidth, boxHeight);
	}

	private GDimension drawSelectedText(GGraphics2D g2, int left, int top,
			boolean draw) {
		GFont font = getLabelFont();

		if (seLatex) {
			return draw ? drawLatex(g2, geoList, font, selectedText, left, top)
					: measureLatex(geoList, font, selectedText, false);
		}

		g2.setFont(font);

		GTextLayout layout = getLayout(g2, selectedText, font);

		final int w = (int) layout.getBounds().getWidth();

		if (draw) {
			EuclidianStatic.drawIndexedString(view.getApplication(), g2,
					selectedText, left, top, false);
		}

		return AwtFactory.getPrototype().newDimension(w,
				(int) Math.round(layout.getDescent() + layout.getAscent()));
	}

	private int getTriangleControlWidth() {
		return selectedDimension.getHeight();
	}

	@Override
	public int getPreferredWidth() {
		if (selectedDimension == null) {
			return 0;
		}
		int selectedWidth = selectedDimension.getWidth()
				+ (isLatexString(selectedText) ? 0 : 2 * COMBO_TEXT_MARGIN)
				+ getTriangleControlWidth();

		int maxItemWidth = drawOptions.getMaxItemWidth();
		return (isOptionsVisible() && maxItemWidth > selectedWidth)
				? maxItemWidth : selectedWidth;
	}

	/**
	 * 
	 * @return The whole width of the widget including the label.
	 */
	public int getTotalWidth() {
		return labelSize.getX() + getPreferredWidth();
	}

	/**
	 * 
	 * @return The height of the combo including the label
	 */
	public int getTotalHeight() {
		int h = labelSize.getY();
		return Math.max(h, boxHeight);
	}

	/**
	 * Returns if mouse is hit the options or not.
	 * 
	 * @param x
	 *            mouse x coordinate
	 * @param y
	 *            mouse y coordinate
	 * @return true if options rectangle hit by mouse.
	 */
	public boolean isOptionsHit(int x, int y) {
		return drawOptions.isHit(x, y);
	}

	/**
	 * Called when mouse is over options to highlight item.
	 * 
	 * @param x
	 *            mouse x-coordinate
	 * @param y
	 *            mouse y-coordinate
	 */
	public void onOptionOver(int x, int y) {
		drawOptions.onMouseOver(x, y);
	}

	/**
	 * Called when user presses down the mouse on the widget.
	 * 
	 * @param x
	 *            Mouse x coordinate.
	 * @param y
	 *            Mouse y coordinate.
	 */
	public void onMouseDown(int x, int y) {
		if (drawOptions.onMouseDown(x, y)) {
			return;
		}

		toggleOptions(x, y);
	}

	private void toggleOptions(int x, int y) {
		DrawDropDownList opened = view.getOpenedComboBox();
		if ((opened == null || !opened.isOptionsHit(x, y))
				&& drawSelected.isOpenButtonHit(x, y)) {
			boolean visible = isOptionsVisible();
			if (!visible) {
				// make sure keyboard controls work for the dropdown
				view.requestFocus();
			}
			setOptionsVisible(!visible);
		}
	}

	/**
	 * Close dropdown
	 */
	public void closeOptions() {
		setOptionsVisible(false);
	}

	/**
	 * @return whether dropdown is visible
	 */
	public boolean isOptionsVisible() {
		return drawOptions.isVisible();
	}

	/**
	 * @param optionsVisible
	 *            change visibility of dropdown items
	 */
	public void setOptionsVisible(boolean optionsVisible) {
		drawOptions.setVisible(optionsVisible);
	}

	/**
	 * toggle visibility of dropdown items
	 */
	public void toggleOptions() {
		drawOptions.toggle();
	}

	/**
	 * Sync selected index to GeoList
	 */
	public void selectCurrentItem() {
		if (!isOptionsVisible()) {
			return;
		}

		drawOptions.selectCurrentItem();
		closeOptions();
	}

	/**
	 * Gets DrawList for geo. No type check.
	 * 
	 * @param app
	 *            The current application.
	 * @param geo
	 *            The geo we like to get the DrawList for.
	 * @return The DrawList for the geo element;
	 * 
	 */
	public static @CheckForNull DrawDropDownList asDrawable(App app, GeoElement geo) {
		DrawableND draw = app.getActiveEuclidianView().getDrawableFor(geo);
		return draw instanceof DrawDropDownList ? (DrawDropDownList) draw : null;
	}

	/**
	 * Moves dropdown selector up or down by one item.
	 * 
	 * @param down
	 *            Sets if selection indicator should move down or up.
	 */
	@Override
	public void moveSelectorVertical(boolean down) {
		drawOptions.moveSelectorVertical(down);
	}

	/**
	 * Moves the selector horizontally, if dropdown have more columns than one.
	 * 
	 * @param left
	 *            Indicates that selector should move left or right.
	 */
	@Override
	public void moveSelectorHorizontal(boolean left) {
		drawOptions.moveSelectorHorizontal(left);
	}

	/**
	 * @return if combo have more columns than one.
	 */
	public boolean isMultiColumn() {
		return model.getColCount() > 1;
	}

	/**
	 * 
	 * @return if list when draw as combo, is selected.
	 */
	public boolean isSelected() {
		return geo.doHighlighting();
	}

	/**
	 * @param x
	 *            drag end x
	 * @param y
	 *            drag end y
	 * @return whether scroll was needed
	 */
	public boolean onDrag(int x, int y) {
		if (scroller.isActive()) {
			return drawOptions.scrollByDrag(x, y);
		}
		return false;
	}

	@Override
	public void onScroll(int x, int y) {
		scroller.scroll();
	}

	/**
	 * @param delta
	 *            wheel scroll value; only sign matters
	 * @return whether scrolling is allowed
	 */
	public boolean onMouseWheel(double delta) {
		scroller.scroll(delta > 0 ? DropDownScrollMode.DOWN : DropDownScrollMode.UP);
		return scroller.isActive();
	}

	@Override
	public void onClick(int x, int y) {
		drawOptions.onClick(x, y);
	}

	/**
	 * Update the opened ComboBox variable for enclosing view
	 */
	void updateOpenedComboBox() {
		DrawDropDownList dl = view.getOpenedComboBox();
		if (drawOptions.isVisible()) {
			view.setOpenedComboBox(this);
		} else if (dl == this) {
			view.setOpenedComboBox(null);
		}
	}

	/**
	 * @param x
	 *            mouse x (within view)
	 * @param y
	 *            mouse y (within view)
	 */
	public void onMouseUp(int x, int y) {
		drawOptions.onMouseUp(x, y);
	}

	@Override
	public int getPreferredHeight() {
		if (selectedDimension == null) {
			return 0;
		}

		int margin = geo.isLabelVisible() ? 2 * COMBO_TEXT_MARGIN : COMBO_TEXT_MARGIN;
		return selectedDimension.getHeight() + margin;
	}

	/**
	 * @param idx index of the hovered item
	 */
	public void setHoverIndex(int idx) {
		drawOptions.setHoverIndex(idx);
	}

	public int getOptionCount() {
		return model.itemCount();
	}

	public int getBoxWidth() {
		return boxWidth;
	}

	public boolean isControlHit(int x, int y) {
		return drawSelected.isOpenButtonHit(x, y);
	}

	@Override
	public boolean isHighlighted() {
		return view.getApplication().getSelectionManager().isKeyboardFocused(geo);
	}
}