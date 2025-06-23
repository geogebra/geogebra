/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.InputBoxBounds;
import org.geogebra.common.euclidian.LatexRendererSettings;
import org.geogebra.common.euclidian.TextRendererSettings;
import org.geogebra.common.euclidian.event.FocusListenerDelegate;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.gui.inputfield.InputMode;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.StringUtil;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Checkbox for free GeoBoolean object.
 * 
 * @author Michael
 */

public class DrawInputBox extends CanvasDrawable {
	// TODO: examine these two, why are they needed and why these values.
	private static final double TF_HEIGHT_FACTOR = 1.22;
	/** ratio of length and screen width */
	private static final double TF_WIDTH_FACTOR = 0.81;
	public static final int TF_MARGIN_VERTICAL = 10;
	/** Padding of the field (plain text) */
	public static final int TF_PADDING_HORIZONTAL = 2;

	public static final int SYMBOLIC_MIN_HEIGHT = 40;
	public static final int MIN_HEIGHT = 24;

	/** input box */
	private final GeoInputBox geoInputBox;

	private boolean isVisible;

	private boolean editing = false;

	private String oldCaption;

	private int oldLength = 0;
	private GFont textFont;
	private TextRenderer textRenderer;
	private GDimension labelDimension = null;
	private GColor borderColor = null;

	private final InputBoxBounds inputBoxBounds;
	private TextRendererSettings rendererSettings;

	/**
	 * @param view
	 *            view
	 * @param geo
	 *            input box
	 */
	public DrawInputBox(EuclidianView view, GeoInputBox geo) {
		this.view = view;
		this.geoInputBox = geo;
		this.geo = geo;
		inputBoxBounds = new InputBoxBounds(geoInputBox);

		if (getTextField() != null) {
			getTextField().addFocusListener(new InputFieldListener());
			getTextField().addKeyHandler(new InputFieldKeyListener());

		}
		textFont = getTextFont(geo.getText());
		update();
	}

	@Override
	public int getCaptionY(boolean laTeX, int captionHeight) {
		return laTeX ? boxTop + boxHeight / 2 - captionHeight / 2
				: yLabel + getTextBottom();
	}

	/**
	 * Listens to events in this input box
	 * 
	 * @author Michael + Judit
	 */
	public class InputFieldListener
			implements FocusListenerDelegate {

		private String initialText;

		@Override
		public void focusGained() {
			if (!isSelectedForInput()) {
				return;
			}

			getView().getEuclidianController().textfieldHasFocus(true);
			updateGeoInputBox();

			initialText = getTextField().getText();
			view.getViewTextField().setBoxVisible(true);
		}

		@Override
		public void focusLost() {
			if (!isSelectedForInput()) {
				return;
			}
			getView().getEuclidianController().textfieldHasFocus(false);

			hideWidget();

			// make sure (expensive) update doesn't happen unless needed
			// also caused problems when Object Properties opened
			if (!getTextField().getText().equals(initialText)) {
				updateModel();
				view.getViewTextField().draw(DrawInputBox.this);
			}

			view.getViewTextField().setBoxVisible(false);
			view.getViewTextField().reset();
		}
	}

	private void updateModel() {
		GeoInputBox inputBox = getGeoInputBox();
		inputBox.textObjectUpdated(getTextField());
		inputBox.textSubmitted();
	}

	/**
	 * Listens to key events in this input box
	 * 
	 * @author Michael + Judit
	 */
	public class InputFieldKeyListener implements KeyHandler {

		/**
		 * Handles new character
		 * 
		 * @param e
		 *            key event
		 */
		@Override
		public void keyReleased(KeyEvent e) {
			if (!isSelectedForInput()) {
				return;
			}

			AutoCompleteTextField tf = getTextField();
			geoInputBox.clearTempUserInput();

			if (e.isEnterKey()) {
				// Force focus removal in IE
				tf.setFocus(false);
				getView().requestFocusInWindow();
				tf.setVisible(false);
				getGeoInputBox().updateLinkedGeo(tf.getText());
			} else {
				GeoElementND linkedGeo = geoInputBox.getLinkedGeo();

				if (linkedGeo instanceof GeoAngle) {

					String text = tf.getText();

					// return if text already contains degree symbol or variable
					for (int i = 0; i < text.length(); i++) {
						if (!StringUtil.isDigit(text.charAt(i))) {
							return;
						}
					}

					int caretPos = tf.getCaretPosition();

					// add degree symbol to end if it's (a) a GeoText and (b)
					// just digits
					tf.setText(text + Unicode.DEGREE_STRING);

					tf.setCaretPosition(caretPos);
				}
			}
		}
	}

	/**
	 * @return whether this drawable is connected to the active input for the
	 *         view
	 */
	private boolean isSelectedForInput() {
		return view.getTextField() != null
				&& view.getTextField().getInputBox() == getGeoInputBox();
	}

	@Override
	final public void update() {
		update(false);
	}

	@Override
	final public void updateForView() {
		update(true);
	}

	private void update(boolean forView) {
		isVisible = geo.isEuclidianVisible();
		updateRenderer();
		if (getTextField() == null) {
			updateLabel();
			updateLabelSize();
			return;
		}

		if (!forView) {
			getTextField().setVisible(false);
			view.getViewTextField().setBoxVisible(false);
		}
		int length = getGeoInputBox().getLength();
		if (length != oldLength && isSelectedForInput()) {
			if (!canSetWidgetPixelSize()) {
				view.getViewTextField().setColumns(length);
			}
			getTextField().prepareShowSymbolButton(
					length > EuclidianConstants.SHOW_SYMBOLBUTTON_MINLENGTH);

			oldLength = length;
		}
		if (!isVisible) {
			if (view.getSymbolicEditor() != null
					&& view.getSymbolicEditor().getDrawInputBox() == this) {
				view.getSymbolicEditor().applyAndHidDeferred();
			}
			return;
		}
		if (view.getSymbolicEditor() != null
				&& view.getSymbolicEditor().getDrawInputBox() == this) {
			view.getSymbolicEditor().updateStyle();
		}

		// show hide label by setting text
		updateLabel();
		if (isSelectedForInput()) {
			updateGeoInputBox();
			updateStyle(getTextField());
		} else {
			textFont = getTextFont(getGeoInputBox().getText());
		}

		view.getViewTextField().revalidateBox();

		updateLabelSize();
		if (geoInputBox.needsUpdatedBoundingBox()) {
			recomputeSize();
		}
		view.getViewTextField().setBoxBounds(labelRectangle);
	}

	private void updateRenderer() {
		if (geoInputBox.isSymbolicMode()) {
			rendererSettings = LatexRendererSettings.createForInputBox(
					geoInputBox.getApp().getFontSize(), geoInputBox.getFontSizeMultiplier());
			textRenderer = new LaTeXTextRenderer(this, rendererSettings);
		} else {
			rendererSettings = new SimpleTextRendererSettings();
			textRenderer = new SimpleTextRenderer(view.getApplication(), this,
					rendererSettings);
		}

		inputBoxBounds.setRenderer(textRenderer);
	}

	private void updateLabelSize() {
		xLabel = getGeoInputBox().getScreenLocX(view);
		yLabel = getGeoInputBox().getScreenLocY(view);
		if (getDynamicCaption() != null && getDynamicCaption().isEnabled()) {
			getDynamicCaption().update();
		}

		labelRectangle.setBounds(xLabel, yLabel, getPreferredWidth(), getPreferredHeight());
	}

	private void updateLabel() {
		if (geo.isLabelVisible()) {
			// get caption to show r
			String caption = geo.getCaption(StringTemplate.defaultTemplate);
			if (!caption.equals(oldCaption)) {
				oldCaption = caption;
				labelDesc = caption; // GeoElement.indicesToHTML(caption, true);
			}
		}
		setLabelFontSize((int) (view.getFontSize()
				* getGeoInputBox().getFontSizeMultiplier()));
	}

	private void updateGeoInputBox() {
		AutoCompleteTextField tf = getTextField();
		if (tf != null) {
			getGeoInputBox().updateText(tf);
			tf.setTextAlignmentsForInputBox(geoInputBox.getAlignment());
		}
	}

	private void updateStyle(AutoCompleteTextField tf) {
		textFont = getTextFont(tf.getText());

		tf.setFont(textFont);

		GColor fgCol = GColor.BLACK;
		GColor bgCol = view.getBackgroundCommon();

		if (geo != null) {
			fgCol = geo.getObjectColor();
			if (geo.getBackgroundColor() != null) {
				bgCol = geo.getBackgroundColor();
			}
		}

		tf.setForeground(fgCol);
		tf.setBackground(bgCol);

		tf.setEditable(true);
	}

	/**
	 * @return whether the last evaluation had errors
	 */
	public boolean hasError() {
		return getGeoInputBox().hasError();
	}

	/**
	 * @param text text to display
	 * @return the font that has the correct size for the input box
	 * and can display the given text
	 */
	public GFont getTextFont(String text) {
		return view.getApplication().getFontCanDisplay(text,
				geoInputBox.isSerifContent() && geoInputBox.isSymbolicMode(),
				GFont.PLAIN, getLabelFontSize());
	}

	/**
	 * @return size of label + input
	 */
	public GDimension getTotalSize() {
		recomputeSize();
		return new GDimension() {

			@Override
			public int getWidth() {
				return labelSize.x + getPreferredWidth();
			}

			@Override
			public int getHeight() {
				return Math.max(labelSize.y, getPreferredHeight());
			}
		};
	}

	@Override
	public int getPreferredWidth() {
		return (int) Math.round(((getView().getApplication().getFontSize()
				* getGeoInputBox().getFontSizeMultiplier())) * getGeoInputBox().getLength()
				* TF_WIDTH_FACTOR);
	}

	@Override
	public int getPreferredHeight() {
		int height = (int) Math.round(((getView().getApplication().getFontSize()
				* getGeoInputBox().getFontSizeMultiplier())) * TF_HEIGHT_FACTOR)
				+ TF_MARGIN_VERTICAL;
		return Math.max(height, MIN_HEIGHT);
	}

	@Override
	final public void draw(GGraphics2D g2) {
		if (isVisible) {
            String txt = getGeoInputBox().getText();
            if (txt != null) {
                setLabelFont(txt);
                drawOnCanvas(g2);
            }
		}
	}

	private void drawInputBoxOnCanvas(GGraphics2D g2) {
		drawBoundsOnCanvas(g2);
		drawTextOnCanvas(g2);
	}

	private void drawBoundsOnCanvas(GGraphics2D g2) {
		GColor bgColor = geo.getBackgroundColor() != null
				? geo.getBackgroundColor()
				: view.getBackgroundCommon();

		AutoCompleteTextField textField = getTextField();

		if (textField != null) {
			inputBoxBounds.update(view, getLabelTop(), textFont, labelDesc);
			textField.drawBounds(g2, bgColor, inputBoxBounds.getBounds());
		}
	}

	/**
	 * Returns the bounds of the input box.
	 * This method is public for testing.
	 *
	 * @return The bounds of the input box.
	 */
	public GRectangle getInputFieldBounds() {
		return inputBoxBounds.getBounds();
	}

	private void drawTextOnCanvas(GGraphics2D g2) {
		String text = getGeoInputBox().getDisplayText();
		g2.setFont(textFont);
		g2.setPaint(geo.getObjectColor());
		drawText(g2, text);
	}

	private void drawText(GGraphics2D g2, String text) {
		int textTop = (int) Math.round(inputBoxBounds.getY());
		textRenderer.drawText(geoInputBox, g2, textFont, text, getTextLeft(), textTop);
	}

	double getContentWidth() {
		return boxWidth - TF_PADDING_HORIZONTAL * 2;
	}

	private int getTextLeft() {
		return boxLeft + TF_PADDING_HORIZONTAL;
	}

	@Override
	protected int getLabelGap() {
		return 2;
	}

	@Override
	public void drawWidget(GGraphics2D g2) {
		final GFont font = g2.getFont();
		g2.setFont(getLabelFont());

		boolean latexLabel = recomputeSize();

		// TF Bounds
		labelRectangle.setBounds(boxLeft, boxTop, boxWidth, boxHeight);

		if (isSelectedForInput()) {
			view.getViewTextField().setBoxBounds(labelRectangle);
		}

		// no painting while editing to avoid double border: both symbolic and non-symbolic
		if ((!editing && !isSelectedForInput()) || view.getApplication().isExporting()) {
			drawInputBoxOnCanvas(g2);
		}

		highlightLabel(g2, latexLabel);
		if (geo.isLabelVisible()) {
			drawLabel(g2, getGeoInputBox(), labelDesc);
		}

		g2.setFont(font);
		if (isSelectedForInput()) {
			view.getViewTextField().repaintBox(g2);
		}

		if (editing && view.getSymbolicEditor() != null) {
			view.getSymbolicEditor().repaintBox(g2);
		}
	}

	private boolean recomputeSize() {
		return measureLabel(getGeoInputBox(), labelDesc);
	}

	@Override
	protected boolean hitWidgetBounds(int x, int y) {
		return geoInputBox.isSymbolicMode()
			? inputBoxBounds.contains(x, y)
			: super.hitWidgetBounds(x, y);
	}

	/**
	 * @return whether the canvas drawing determines the autocomplete text field
	 *         size (if not, they must be computed separately)
	 */
	private boolean canSetWidgetPixelSize() {
		return !view.getApplication().isDesktop();
	}

	private void drawLabel(GGraphics2D g2, GeoElement geo0, String text) {
		if (getDynamicCaption() != null && getDynamicCaption().isEnabled()) {
			getDynamicCaption().draw(g2);
		} else if (isLatexString(text)) {
			labelDimension = drawLatex(g2, geo0, getLabelFont(), text, xLabel, (int) getLabelTop());
		} else {
			g2.setPaint(geo.getObjectColor());

			EuclidianStatic.drawIndexedString(view.getApplication(), g2, text,
					xLabel, yLabel + getTextBottom(), false, null, null);
		}
	}

	/**
	 * @param str
	 *            input string
	 */
	public void setFocus(final String str) {
		if (str != null && !"\t".equals(str)) {
			getTextField().wrapSetText(str);
		}
		getTextField().requestFocus();
	}

	private void updateBoxPosition() {
		if (!isSelectedForInput()) {
			return;
		}

		view.getViewTextField().revalidateBox();
		recomputeSize();
		labelRectangle.setBounds(boxLeft,
				computeBoxTop(getPreferredHeight()),
				getPreferredWidth(),
				getPreferredHeight());
		view.getViewTextField().setBoxBounds(labelRectangle);
	}

	/**
	 * Compute the top of the box based on the position of the label, the height
	 * of the label and the height of the box
	 * @param height height of the box
	 * @return y coordinate of the box
	 */
	public int computeBoxTop(double height) {
		double labelHeight = getHeightForLabel(labelDesc);
		return (int) Math.floor(getLabelTop() + ((labelHeight - height) / 2));
	}

	/**
	 * @param show whether to show the widget
	 */
	public void setWidgetVisible(boolean show) {
		if (geo.isEuclidianVisible() && view.isVisibleInThisView(geo) && show) {
			showWidget();
		} else {
			hideWidget();
		}
	}

	private void showWidget() {
		if (geoInputBox.isSymbolicMode()) {
			attachMathField(view.getEuclidianController().getMouseLoc());
			return;
		}

		view.cancelBlur();
		view.getViewTextField().revalidateBox();
		view.getViewTextField().setBoxVisible(true);
		attachTextField();
		if (!view.getEuclidianController().isTemporaryMode()) {
			getTextField().requestFocus();
		}
	}

	/**
	 * Get view's input box and attach to this
	 */
	public void attachTextField() {
		hideSymbolicField();
		updateBoxPosition();
		AutoCompleteTextField tf = getTextField();

		tf.setInputMode(needsNumberInput() ? InputMode.DECIMAL : InputMode.TEXT);
		tf.setDrawTextField(this);
		tf.setUsedForInputBox(getGeoInputBox());
		tf.setVisible(true);

		if (canSetWidgetPixelSize()) {
			tf.setPrefSize(getPreferredWidth(), getPreferredHeight());
		} else {
			view.getViewTextField().setColumns(getGeoInputBox().getLength());
		}
		tf.setText(getGeoInputBox().getText());

		setLabelFontSize((int) (view.getFontSize()
				* getGeoInputBox().getFontSizeMultiplier()));

		updateStyle(tf);
		tf.showPopupSymbolButton(false);
		tf.prepareShowSymbolButton(geoInputBox.needsSymbolButton());
	}

	private boolean needsNumberInput() {
		return geoInputBox.getLinkedGeo().isGeoNumeric();
	}

	/**
	 * Attach the symbolic editor
	 * @param caretPos caret position relative to view
	 */
	public void attachMathField(GPoint caretPos) {
		recomputeSize();
		hideTextField();
		updateRenderer();
		inputBoxBounds.update(view, getLabelTop(), textFont, labelDesc);
		view.attachSymbolicEditor(geoInputBox, inputBoxBounds.getBounds(),
				rendererSettings, caretPos);
		update();
		view.repaintView();
	}

	/**
	 * Hide the widget.
	 */
	protected void hideWidget() {
		if (!isSelectedForInput()) {
			return;
		}

		if (geoInputBox.isSymbolicMode()) {
			hideSymbolicField();
		} else {
			hideTextField();
		}

	}

	private void hideSymbolicField() {
		view.hideSymbolicEditor();
	}

	private void hideTextField() {
		view.getViewTextField().hideDeferred();
	}

	/**
	 * @return input box (can be null if no implementation ie iOS, Android)
	 */
	public AutoCompleteTextField getTextField() {
		return view.getTextField(getGeoInputBox(), this);
	}

	/**
	 * @return input box
	 */
	GeoInputBox getGeoInputBox() {
		return geoInputBox;
	}

	/**
	 *
	 * @return if the GeoInputBox is under editing.
	 */
	public boolean isEditing() {
		return editing;
	}

	/**
	 * Set this true if an editor is active for this input box
	 * or false if it is not.
	 *
	 * @param editing to set
	 */
	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	/**
	 * @return height of the label, depending on whether it was latex or not
	 */
	int getHeightForLabel(String label) {
		return isLatexString(label) && labelDimension != null ? labelDimension.getHeight()
				: getLabelTextHeight();
	}

	public void setBorderColor(GColor borderColor) {
		this.borderColor = borderColor;
	}

	public GColor getBorderColor() {
		return borderColor;
	}

	@Override
	public boolean isHighlighted() {
		return view.getApplication().getSelectionManager().isKeyboardFocused(geo);
	}
}
