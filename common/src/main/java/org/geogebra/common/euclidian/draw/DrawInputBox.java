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
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.euclidian.event.FocusListener;
import org.geogebra.common.euclidian.event.FocusListenerDelegate;
import org.geogebra.common.euclidian.event.GFocusEvent;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.lang.Unicode;

//import javax.swing.SwingUtilities;

/**
 * Checkbox for free GeoBoolean object.
 * 
 * @author Michael
 */
public class DrawInputBox extends CanvasDrawable implements RemoveNeeded {
	// TODO: examine these two, why are they needed and why these values.
	private static final double TF_HEIGHT_FACTOR = 1.22;
	/** ratio of length and screen width */
	public static final double TF_WIDTH_FACTOR = 0.81;

	private static final int TF_MARGIN = 10;

	/** textfield */
	final GeoInputBox geoInputBox;

	private boolean isVisible;

	private String oldCaption;
	/** textfield component */
	// ButtonListener bl;
	private InputFieldListener ifListener;
	private KeyHandler ifKeyListener;

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

		// action listener for checkBox
		// bl = new ButtonListener();
		ifListener = new InputFieldListener();
		ifKeyListener = new InputFieldKeyListener();

		// ((geogebra.gui.inputfield.AutoCompleteTextField)
		// textField).addFocusListener(bl);
		if (getTextField() != null) {
			getTextField().addFocusListener(
					AwtFactory.getPrototype().newFocusListener(ifListener));
			// label.addMouseListener(bl);
			// label.addMouseMotionListener(bl);
			getTextField().addKeyHandler(ifKeyListener);

		}
		update();
	}

	/**
	 * @return the text field
	 */
	GeoElement getGeo() {
		return geo;
	}

	/**
	 * Listens to events in this textfield
	 * 
	 * @author Michael + Judit
	 */
	public class InputFieldListener extends FocusListener
			implements FocusListenerDelegate {

		private String initialText;

		/**
		 * @param e
		 *            focus event
		 */
		public void focusGained(GFocusEvent e) {
			if (!isSelectedForInput()) {
				return;
			}

			getView().getEuclidianController().textfieldHasFocus(true);
			geoInputBox.updateText(getTextField());

			initialText = getTextField().getText();
		}

		/**
		 * @param e
		 *            focus event
		 */
		public void focusLost(GFocusEvent e) {
			if (!isSelectedForInput()) {
				return;
			}
			getView().getEuclidianController().textfieldHasFocus(false);

			// GGB-22 revert r43455
			// stops alpha popup working
			hideWidget();

			// make sure (expensive) update doesn't happen unless needed
			// also caused problems when Object Properties opened
			if (!getTextField().getText().equals(initialText)) {
				geoInputBox.textObjectUpdated(getTextField());
				geoInputBox.textSubmitted();
				draw(getView().getGraphicsForPen());
			}
		}
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
				Log.debug("[DF] keyReleased NOT " + "for " + labelDesc);
				return;
			}
			Log.debug("[DF] keyReleased for " + labelDesc);

			AutoCompleteTextField tf = getTextField();

			if (e.isEnterKey()) {
				// Force focus removal in IE
				tf.setFocus(false);
				getView().requestFocusInWindow();
				tf.setVisible(false);
				draw(getView().getGraphicsForPen());
				geoInputBox.setText(tf.getText());
			} else {
				GeoElementND linkedGeo = ((GeoInputBox) getGeo())
						.getLinkedGeo();

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
					tf.setText(text + Unicode.DEGREE);

					tf.setCaretPosition(caretPos);
				}
			}
		}
	}

	/**
	 * @return whether this drawable is connected to the active input for the
	 *         view
	 */
	boolean isSelectedForInput() {
		return view.getTextField() != null
				&& view.getTextField().getInputBox() == geoInputBox;
	}

	private int oldLength = 0;
	private GFont textFont;

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
		if (getTextField() == null) {
			return;
		}
		if (!forView) {
			getTextField().setVisible(false);
			getBox().setVisible(false);
		}

		int length = geoInputBox.getLength();
		if (length != oldLength) {
			getTextField().setColumns(length);
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
				labelDesc = caption;// GeoElement.indicesToHTML(caption, true);

			}
		}

		setLabelFontSize((int) (view.getFontSize()
				* geoInputBox.getFontSizeMultiplier()));

		updateGeoIntputBox();

		updateStyle(getTextField());

		getBox().revalidate();

		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;

		labelRectangle.setBounds(xLabel, yLabel, getPreferredSize().getWidth(),
				getPreferredSize().getHeight());
		getBox().setBounds(labelRectangle);
	}

	private void updateGeoIntputBox() {
		geoInputBox.updateText(getTextField());

	}

	private void updateStyle(AutoCompleteTextField tf) {
		GFont vFont = view.getFont();
		textFont = view.getApplication().getFontCanDisplay(tf.getText(), false,
				vFont.getStyle(), getLabelFontSize());

		tf.setOpaque(true);
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

		tf.setFocusable(true);
		tf.setEditable(true);
	}

	/**
	 * @return size of label + input
	 */
	public GDimension getTotalSize() {
		measureLabel(view.getGraphicsForPen(), geoInputBox, labelDesc);
		return new GDimension() {

			@Override
			public int getWidth() {
				return labelSize.x + getPreferredSize().getWidth();
			}

			@Override
			public int getHeight() {
				return Math.max(labelSize.y, getPreferredSize().getHeight());
			}
		};
	}

	@Override
	public GDimension getPreferredSize() {
		// TODO: eliminate magic numbers
		return new GDimension() {

			@Override
			public int getWidth() {
				return (int) Math
						.round(((getView().getApplication().getFontSize()
								* geoInputBox.getFontSizeMultiplier()))
								* geoInputBox.getLength() * TF_WIDTH_FACTOR);
			}

			@Override
			public int getHeight() {
				return (int) Math
						.round(((getView().getApplication().getFontSize()
								* geoInputBox.getFontSizeMultiplier()))
								* TF_HEIGHT_FACTOR)
						+ TF_MARGIN;

			}
		};
	}

	@Override
	final public void draw(GGraphics2D g2) {
		if (isVisible) {
			drawOnCanvas(g2, geoInputBox.getText());
		}
	}

	@Override
	protected void drawWidget(GGraphics2D g2) {
		GFont font = g2.getFont();
		g2.setFont(getLabelFont().deriveFont(GFont.PLAIN));

		boolean latexLabel = measureLabel(g2, geoInputBox, labelDesc);
		int textLeft = boxLeft + 2;
		int textBottom = boxTop + getTextBottom();

		// TF Bounds

		labelRectangle.setBounds(boxLeft - 1, boxTop - 1, boxWidth,
				boxHeight - 3);
		if (isSelectedForInput()) {
			getBox().setBounds(labelRectangle);

		}
		GColor bgColor = geo.getBackgroundColor() != null
				? geo.getBackgroundColor() : view.getBackgroundCommon();
		getTextField().drawBounds(g2, bgColor, boxLeft, boxTop, boxWidth,
				boxHeight);

		highlightLabel(g2, latexLabel);

		g2.setPaint(geo.getObjectColor());

		if (geo.isLabelVisible()) {
			drawLabel(g2, geoInputBox, labelDesc);
		}

		String text = geoInputBox.getText();

		g2.setFont(textFont.deriveFont(GFont.PLAIN));

		EuclidianStatic.drawIndexedString(view.getApplication(), g2,
				text.substring(0, getTruncIndex(text, g2)), textLeft,
				textBottom, false);
		g2.setFont(font);
		if (isSelectedForInput()) {
			getBox().repaint(g2);
		}
	}

	private int getTruncIndex(String text, GGraphics2D g2) {
		int idx = text.length();
		int mt = measureTextWidth(text, g2.getFont(), g2);
		while (mt > boxWidth && idx > 0) {
			idx--;
			mt = measureTextWidth(text.substring(0, idx), g2.getFont(), g2);

		}
		return idx;
	}

	/**
	 * Removes button from view again
	 */
	@Override
	final public void remove() {
		if (!isSelectedForInput()) {
			return;
		}
		// view.remove(getBox());
	}

	/**
	 * @param str
	 *            input string
	 */
	public void setFocus(final String str) {
		Log.debug("[DrawTextFied] setFocus: " + str);
		if (str != null && !"\t".equals(str)) {
			getTextField().wrapSetText(str);
		}
		getTextField().requestFocus();

	}

	private void updateBoxPosition() {
		if (!isSelectedForInput()) {
			return;
		}

		getBox().revalidate();
		measureLabel(view.getGraphicsForPen(), geoInputBox, labelDesc);
		labelRectangle.setBounds(boxLeft, boxTop, getPreferredSize().getWidth(),
				getPreferredSize().getHeight());
		getBox().setBounds(labelRectangle);
	}

	@Override
	protected void showWidget() {
		GeoElement last = view.getTextField().getDrawTextField()
				.getGeoElement();
		if (last != null) {
			Log.debug("LAST: " + last);

		}
		view.cancelBlur();
		getBox().revalidate();
		getBox().setVisible(true);
		attachTextField();
		if (!view.getEuclidianController().isTemporaryMode()) {
			getTextField().requestFocus();
		}
	}

	/**
	 * Get view's textfield and attach to this
	 */
	public void attachTextField() {

		updateBoxPosition();
		AutoCompleteTextField tf = getTextField();

		tf.setDrawTextField(this);
		tf.setUsedForInputBox(geoInputBox);
		tf.setVisible(true);
		tf.setColumns(geoInputBox.getLength());
		tf.setText(geoInputBox.getText());

		setLabelFontSize((int) (view.getFontSize()
				* geoInputBox.getFontSizeMultiplier()));

		updateStyle(tf);

		tf.showPopupSymbolButton(false);

		if (geoInputBox
				.getLength() < EuclidianConstants.SHOW_SYMBOLBUTTON_MINLENGTH
				|| geoInputBox.getLinkedGeo() instanceof GeoText) {
			tf.prepareShowSymbolButton(false);
		} else {
			tf.prepareShowSymbolButton(true);
		}

	}

	@Override
	protected void hideWidget() {

		if (!isSelectedForInput()) {
			return;
		}

		getTextField().hideDeferred(getBox());

	}

	/**
	 * Hide the widget
	 */
	public void removeTextField() {
		hideWidget();
	}

	/**
	 * @return textfield
	 */
	public AutoCompleteTextField getTextField() {
		AutoCompleteTextField tf = view.getTextField(geoInputBox, this);
		return tf;

	}

	private GBox getBox() {
		return view.getBoxForTextField();
	}

	/**
	 * Writes the real textfield's value to the GeoInputBox.
	 */
	public void apply() {
		geoInputBox.setText(getTextField().getText());
	}

	@Override
	public BoundingBox getBoundingBox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateBoundingBox() {
		// TODO Auto-generated method stub
		
	}

}
