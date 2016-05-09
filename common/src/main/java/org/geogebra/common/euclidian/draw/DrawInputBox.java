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
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.euclidian.event.FocusListener;
import org.geogebra.common.euclidian.event.GFocusEvent;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;

//import javax.swing.SwingUtilities;

/**
 * Checkbox for free GeoBoolean object.
 * 
 * @author Michael
 */
public class DrawInputBox extends CanvasDrawable implements RemoveNeeded {
	// TODO: examine these two, why are they needed and why these values.
	private static final double TF_HEIGHT_FACTOR = 1.22;
	private static final double TF_WIDTH_FACTOR = 0.81;

	private static final int TF_PADDING = 5;

	private static final int TF_MARGIN = 10;

	/** textfield */
	final GeoInputBox geoInputBox;

	private boolean isVisible;

	private String oldCaption;
	/** textfield component */
	AutoCompleteTextField textField;
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
		box = geo.getKernel().getApplication().getSwingFactory()
				.createHorizontalBox(view.getEuclidianController());
		// action listener for checkBox
		// bl = new ButtonListener();
		ifListener = new InputFieldListener();
		ifKeyListener = new InputFieldKeyListener();
		textField = geoInputBox.getTextField(view.getViewID(), this);// SwingFactory.prototype.newAutoCompleteTextField(geo.getLength(),
		textField.setDeferredFocus(true);

		// view.getApplication(),
		// this);
		// this will be set in update(): textField.showPopupSymbolButton(true);
		textField.setAutoComplete(false);
		textField.enableColoring(false);


		// label.setLabelFor(textField); <- next row
		// textField.setLabel(label);

		textField.setVisible(true);
		// ((geogebra.gui.inputfield.AutoCompleteTextField)
		// textField).addFocusListener(bl);
		textField.addFocusListener(
				AwtFactory.prototype.newFocusListener(ifListener));
		// label.addMouseListener(bl);
		// label.addMouseMotionListener(bl);
		textField.addKeyHandler(ifKeyListener);
		box.add(textField);

		view.add(box);

		// Add mouse listeners to textField so that it becomes draggable
		// on a right click. These listeners are registered first to prevent
		// the JTextField listeners from initiating editing.
		/*
		 * MouseListener[] ml = textField.getMouseListeners(); for(int i = 0;
		 * i<ml.length; i++){ textField.removeMouseListener(ml[i]); }
		 * MouseMotionListener[] mml = textField.getMouseMotionListeners();
		 * for(int i = 0; i<mml.length; i++){
		 * textField.removeMouseMotionListener(mml[i]); }
		 * 
		 * textField.addMouseListener(bl); for(int i = 0; i<mml.length; i++){
		 * textField.addMouseMotionListener(mml[i]); }
		 * 
		 * textField.addMouseMotionListener(bl); for(int i = 0; i<ml.length;
		 * i++){ textField.addMouseListener(ml[i]); }
		 */

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
	public class InputFieldListener extends FocusListener {

		private String initialText;

		/**
		 * Creates new listener
		 */
		public InputFieldListener() {
			// TODO Auto-generated constructor stub
		}

		/**
		 * @param e
		 *            focus event
		 */
		public void focusGained(GFocusEvent e) {
			getView().getEuclidianController().textfieldHasFocus(true);
			geoInputBox.updateText(textField);

			initialText = textField.getText();
		}

		/**
		 * @param e
		 *            focus event
		 */
		public void focusLost(GFocusEvent e) {
			getView().getEuclidianController().textfieldHasFocus(false);

			// GGB-22 revert r43455
			// stops alpha popup working
			hideWidget();

			// make sure (expensive) update doesn't happen unless needed
			// also caused problems when Object Properties opened
			if (!textField.getText().equals(initialText)) {
				geoInputBox.textObjectUpdated(textField);
				geoInputBox.textSubmitted();
				draw(view.getGraphicsForPen());
			}
		}
	}

	private void updateCanvas() {

	}

	/**
	 * Listens to key events in this textfield
	 * 
	 * @author Michael + Judit
	 */
	public class InputFieldKeyListener implements KeyHandler {

		/**
		 * Creates new listener
		 */
		public InputFieldKeyListener() {
			// TODO Auto-generated constructor stub
		}

		/**
		 * Handles new chracter
		 * 
		 * @param e
		 *            key event
		 */
		public void keyReleased(KeyEvent e) {
			if (e.isEnterKey()) {
				// Force focus removal in IE
				textField.setFocus(false);
				getView().requestFocusInWindow();
				textField.setVisible(false);
				draw(view.getGraphicsForPen());
				geoInputBox.setText(textField.getText());
			} else {
				GeoElement linkedGeo = ((GeoInputBox) getGeo()).getLinkedGeo();

				if (linkedGeo instanceof GeoAngle) {

					String text = textField.getText();

					// return if text already contains degree symbol or variable
					for (int i = 0; i < text.length(); i++) {
						if (!StringUtil.isDigit(text.charAt(i)))
							return;
					}

					int caretPos = textField.getCaretPosition();

					// add degree symbol to end if it's (a) a GeoText and (b)
					// just digits
					textField.setText(text + Unicode.DEGREE);

					textField.setCaretPosition(caretPos);
				}
			}
		}
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
		if (!forView) {
			textField.setVisible(false);
			box.setVisible(false);
		}

		int length = geoInputBox.getLength();
		if (length != oldLength) {
			textField.setColumns(length);
			textField.prepareShowSymbolButton(
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
 * geoInputBox
				.getFontSizeMultiplier()));
		App app = view.getApplication();

		GFont vFont = view.getFont();
		textFont = app.getFontCanDisplay(textField.getText(), false,
				vFont.getStyle(), getLabelFontSize());

		textField.setOpaque(true);
		textField.setFont(textFont);
		if (geo != null) {
			textField.setForeground(geo.getObjectColor());
		}

		GColor bgCol = geo.getBackgroundColor();
		textField.setBackground(
				bgCol != null ? bgCol : view.getBackgroundCommon());

		textField.setFocusable(true);
		textField.setEditable(true);

		geoInputBox.updateText(textField);
		box.revalidate();

		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;

		setPreferredSize(box.getPreferredSize());

		labelRectangle.setBounds(xLabel, yLabel, getPreferredSize().getWidth(),
				getPreferredSize().getHeight());
		box.setBounds(labelRectangle);
	}

	@Override
	public GDimension getPreferredSize() {
		// TODO: eliminate magic numbers
		return new GDimension() {

			@Override
			public int getWidth() {
				return (int) Math.round(((view.getApplication().getFontSize()
 * geoInputBox
								.getFontSizeMultiplier()))
								* geoInputBox.getLength() * TF_WIDTH_FACTOR);
			}

			@Override
			public int getHeight() {
				return (int) Math.round(((view.getApplication().getFontSize()
 * geoInputBox
								.getFontSizeMultiplier()))
						* TF_HEIGHT_FACTOR) + TF_MARGIN;

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
		box.setBounds(labelRectangle);
		GColor bgColor = geo.getBackgroundColor() != null
				? geo.getBackgroundColor() : view.getBackgroundCommon();
		textField.drawBounds(g2, bgColor, boxLeft, boxTop,
				boxWidth, boxHeight);

		highlightLabel(g2, latexLabel);

		g2.setPaint(geo.getObjectColor());

		if (geo.isLabelVisible()) {
			drawLabel(g2, geoInputBox, labelDesc);
		}

		String text = geoInputBox.getText();

		g2.setFont(textFont.deriveFont(GFont.PLAIN));

		EuclidianStatic.drawIndexedString(view.getApplication(), g2,
				text.substring(0, getTruncIndex(text, g2)), textLeft,
				textBottom, false,
				false);
		g2.setFont(font);
	}


	private int getTruncIndex(String text, GGraphics2D g2) {
		int idx = text.length();
		int mt = g2.getFontRenderContext().measureTextWidth(text, g2.getFont());
		while (mt > boxWidth && idx > 0) {
			idx--;
			mt = g2.getFontRenderContext()
					.measureTextWidth(text.substring(0, idx), g2.getFont());

		}
		return idx;
	}



	/**
	 * Removes button from view again
	 */
	final public void remove() {
		view.remove(box);
	}

	/**
	 * @param str
	 *            input string
	 */
	public void setFocus(final String str) {
		Log.debug("[DrawTextFied] setFocus");

		textField.requestFocus();
		if (str != null && !str.equals("\t")) {
			textField.wrapSetText(str);
		}

	}

	@Override
	protected void showWidget() {
		view.cancelBlur();
		textField.setVisible(true);

		box.setVisible(true);
		if (!view.getEuclidianController().isTemporaryMode()) {
			textField.requestFocus();
		}
	}

	@Override
	protected void hideWidget() {
		textField.hideDeferred(box);

	}
}
