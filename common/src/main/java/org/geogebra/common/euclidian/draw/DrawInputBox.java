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
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
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

	/** textfield */
	private final GeoInputBox geoInputBox;

	private boolean isVisible;

	private boolean editing = false;

	private String oldCaption;

	private int oldLength = 0;
	private GFont textFont;
	private TextRenderer textRenderer;
	private GDimension labelDimension = null;
	private final DrawDynamicCaption drawDynamicCaption;

	/**
	 * @param view
	 *            view
	 * @param geo
	 *            textfield
	 */
	public DrawInputBox(EuclidianView view, GeoInputBox geo) {
		this.view = view;
		this.geoInputBox = geo;
		this.geo = geo;

		if (getTextField() != null) {
			getTextField().addFocusListener(new InputFieldListener());
			getTextField().addKeyHandler(new InputFieldKeyListener());

		}
		textFont = getTextFont(geo.getText());
		drawDynamicCaption = new DrawDynamicCaption(view, this);
		update();
	}

	/**
	 * Listens to events in this textfield
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
				draw(getView().getGraphicsForPen());
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
	 * Listens to key events in this textfield
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
		if (geoInputBox.isSymbolicMode()) {
			textRenderer = new LaTeXTextRenderer(this);
		} else {
			textRenderer = new SimpleTextRenderer(view.getApplication(), this);
		}

		if (getTextField() == null) {
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
			return;
		}

		// show hide label by setting text
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
		if (isSelectedForInput()) {
			updateGeoInputBox();
			updateStyle(getTextField());
		} else {
			textFont = getTextFont(getGeoInputBox().getText());
		}

		view.getViewTextField().revalidateBox();

		xLabel = getGeoInputBox().getScreenLocX(view);
		yLabel = getGeoInputBox().getScreenLocY(view);
		drawDynamicCaption.update();

		labelRectangle.setBounds(xLabel, yLabel, getPreferredWidth(), getPreferredHeight());

		view.getViewTextField().setBoxBounds(labelRectangle);
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
	protected void highlightLabel(GGraphics2D g2, boolean latex) {
		if (drawDynamicCaption.isEnabled()) {
			drawDynamicCaption.highlight();
		} else {
			super.highlightLabel(g2, latex);
		}
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

	private void drawTextfieldOnCanvas(GGraphics2D g2) {
		drawBoundsOnCanvas(g2);
		drawTextOnCanvas(g2);
	}

	private void drawBoundsOnCanvas(GGraphics2D g2) {
		GColor bgColor = geo.getBackgroundColor() != null
				? geo.getBackgroundColor()
				: view.getBackgroundCommon();

		AutoCompleteTextField textField = getTextField();
		if (textField != null) {
			textField.drawBounds(g2, bgColor, getInputFieldBounds(g2));
		}
	}

	/**
	 * Returns the bounds of the input box.
	 * This method is public for testing.
	 *
	 * @return The bounds of the input box.
	 */
	public GRectangle getInputFieldBounds() {
		return getInputFieldBounds(view.getGraphicsForPen());
	}

	private GRectangle getInputFieldBounds(GGraphics2D g2) {
		return textRenderer.measureBounds(g2, geoInputBox,  textFont, labelDesc);
	}

	private void drawTextOnCanvas(GGraphics2D g2) {
		String text = getGeoInputBox().getDisplayText();
		g2.setFont(textFont);
		g2.setPaint(geo.getObjectColor());
		drawText(g2, text);
	}

	private void drawText(GGraphics2D g2, String text) {
		int textTop = (int) Math.round(getInputFieldBounds(g2).getY());
		textRenderer.drawText(geoInputBox, g2, textFont, text, getTextLeft(), textTop);
	}

	double getContentWidth() {
		return boxWidth - TF_PADDING_HORIZONTAL * 2;
	}

	private int getTextLeft() {
		return boxLeft + TF_PADDING_HORIZONTAL;
	}

	int getTextBottom() {
		return (getPreferredHeight() / 2) + (int) (getLabelFontSize() * 0.4);
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

		// no painting while editing to avoid double border: both symbolic and nonsymbolic
		if ((!editing && !isSelectedForInput()) || view.getApplication().isExporting()) {
			drawTextfieldOnCanvas(g2);
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
		return measureLabel(view.getGraphicsForPen(), getGeoInputBox(), labelDesc);
	}

	@Override
	protected boolean measureLabel(GGraphics2D g2, GeoElement geo0, String text) {
		return drawDynamicCaption.isEnabled()
				? drawDynamicCaption.setLabelSize()
				: super.measureLabel(g2, geo0, text);
	}

	@Override
	protected boolean hitWidgetBounds(int x, int y) {
		return geoInputBox.isSymbolicMode()
			? getInputFieldBounds().contains(x, y)
			: super.hitWidgetBounds(x, y);
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return super.hit(x, y, hitThreshold)
				|| drawDynamicCaption.hit(x, y, hitThreshold);
	}

	@Override
	protected int getLabelTextHeight() {
		return drawDynamicCaption.isEnabled()
				? drawDynamicCaption.getHeight()
				: super.getLabelTextHeight();
	}

	/**
	 * @return whether the canvas drawing determines the autocomplete text field
	 *         size (if not, they must be computed separately)
	 */
	private boolean canSetWidgetPixelSize() {
		return !view.getApplication().isDesktop();
	}

	private void drawLabel(GGraphics2D g2, GeoElement geo0, String text) {
		if (drawDynamicCaption.isEnabled()) {
			drawDynamicCaption.draw(g2);
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
			recomputeSize();
			attachMathField();
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
	 * Get view's textfield and attach to this
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
	 */
	public void attachMathField() {
		hideTextField();
		view.attachSymbolicEditor(geoInputBox, getInputFieldBounds());
		geoInputBox.update();
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
	 * @return textfield (can be null if no implementation ie iOS, Android)
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
	 * @param editing
	 * 			to set.
	 */
	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	/**
	 *
	 * @return height of the label depending of whether it was latex or not
	 */
	int getHeightForLabel(String label) {
		return isLatexString(label) && labelDimension != null ? labelDimension.getHeight()
				: getLabelTextHeight();
	}
}
