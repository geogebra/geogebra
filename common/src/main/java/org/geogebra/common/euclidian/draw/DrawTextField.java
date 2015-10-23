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
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.euclidian.event.FocusEvent;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.GLabel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoTextField;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;

//import javax.swing.SwingUtilities;

/**
 * Checkbox for free GeoBoolean object.
 * 
 * @author Michael
 */
public class DrawTextField extends CanvasDrawable implements RemoveNeeded {
	// TODO: examine these two, why are they needed and why these values.
	private static final double TF_HEIGHT_FACTOR = 1.22;
	private static final double TF_WIDTH_FACTOR = 0.81;

	private static final int TF_PADDING = 5;

	private static final int TF_MARGIN = 10;

	/** textfield */
	final GeoTextField geoTextField;

	private boolean isVisible;

	private String oldCaption;
	/** textfield component */
	AutoCompleteTextField textField;
	private GLabel label;
	// ButtonListener bl;
	private InputFieldListener ifListener;
	private KeyHandler ifKeyListener;


	/**
	 * @param view
	 *            view
	 * @param geo
	 *            textfield
	 */
	public DrawTextField(EuclidianView view, GeoTextField geo) {
		this.view = view;
		this.geoTextField = geo;
		this.geo = geo;
		setDrawingOnCanvas(
				view.getApplication().has(Feature.DRAW_INPUTBOXES_TO_CANVAS));
		box = geo.getKernel().getApplication().getSwingFactory()
				.createHorizontalBox(view.getEuclidianController());
		// action listener for checkBox
		// bl = new ButtonListener();
		ifListener = new InputFieldListener();
		ifKeyListener = new InputFieldKeyListener();
		textField = geoTextField.getTextField(view.getViewID(), this);// SwingFactory.prototype.newAutoCompleteTextField(geo.getLength(),
		if (isDrawingOnCanvas()) {
			textField.setDeferredFocus(true);
		}

		// view.getApplication(),
		// this);
		// this will be set in update(): textField.showPopupSymbolButton(true);
		textField.setAutoComplete(false);
		textField.enableColoring(false);

		// TODO: remove field label totally when DRAW_INPUTBOXES_TO_CANVAS
		// removed
		if (!isDrawingOnCanvas()) {
			label = geo.getKernel().getApplication().getSwingFactory()
				.newJLabel("Label", false);
		}

		// label.setLabelFor(textField); <- next row
		// textField.setLabel(label);

		textField.setVisible(true);
		if (label != null) {
			label.setVisible(true);
		}
		// ((geogebra.gui.inputfield.AutoCompleteTextField)
		// textField).addFocusListener(bl);
		textField.addFocusListener(
				AwtFactory.prototype.newFocusListener(ifListener));
		// label.addMouseListener(bl);
		// label.addMouseMotionListener(bl);
		textField.addKeyHandler(ifKeyListener);
		if (!isDrawingOnCanvas()) {
			box.add(label);
		}
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
	public class InputFieldListener
			extends org.geogebra.common.euclidian.event.FocusListener {

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
		public void focusGained(FocusEvent e) {
			getView().getEuclidianController().textfieldHasFocus(true);
			geoTextField.updateText(textField);

			initialText = textField.getText();
		}

		/**
		 * @param e
		 *            focus event
		 */
		public void focusLost(FocusEvent e) {
			getView().getEuclidianController().textfieldHasFocus(false);

			// GGB-22 revert r43455
			// stops alpha popup working
			// if (isDrawingOnCanvas()) {
			// hideWidget();
			// }

			// make sure (expensive) update doesn't happen unless needed
			// also caused problems when Object Properties opened
			if (!textField.getText().equals(initialText)) {
				geoTextField.textObjectUpdated(textField);
				geoTextField.textSubmitted();
				if (isDrawingOnCanvas()) {
					draw(view.getGraphicsForPen());
				}
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
	public class InputFieldKeyListener
			implements org.geogebra.common.euclidian.event.KeyHandler {

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
				if (isDrawingOnCanvas()) {
					textField.setVisible(false);
					draw(view.getGraphicsForPen());
					geoTextField.setText(textField.getText());
				}
			} else {
				GeoElement linkedGeo = ((GeoTextField) getGeo()).getLinkedGeo();

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
		isVisible = geo.isEuclidianVisible();

		textField.setVisible(isDrawingOnCanvas() ? false : isVisible);
		if (label != null) {
			label.setVisible(isDrawingOnCanvas() ? false : isVisible);
		}

		box.setVisible(isDrawingOnCanvas() ? false : isVisible);
		int length = geoTextField.getLength();
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
			if (label != null) {
				label.setText(labelDesc);
			}
		} else {
			// put back to "" from " " so that the position is the same in
			// ggb40 and ggb42
			if (label != null) {
				label.setText("");
			}
		}

		setLabelFontSize((int) (view.getFontSize()
				* geoTextField.getFontSizeMultiplier()));
		App app = view.getApplication();

		GFont vFont = view.getFont();
		textFont = app.getFontCanDisplay(textField.getText(), false,
				vFont.getStyle(), getLabelFontSize());

		textField.setOpaque(true);
		if (label != null) {
			label.setOpaque(false);
			label.setFont(textFont);
			label.setForeground(geo.getObjectColor());
		}
		textField.setFont(textFont);
		if (geo != null) {
			textField.setForeground(geo.getObjectColor());
		}

		GColor bgCol = geo.getBackgroundColor();
		textField.setBackground(
				bgCol != null ? bgCol : view.getBackgroundCommon());

		textField.setFocusable(true);
		textField.setEditable(true);

		geoTextField.updateText(textField);
		box.revalidate();

		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;

		setPreferredSize(box.getPreferredSize());

		labelRectangle.setBounds(xLabel, yLabel, getPreferredSize().getWidth(),
				getPreferredSize().getHeight());
		box.setBounds(labelRectangle);
		if (!isDrawingOnCanvas()) {
			draw(view.getGraphicsForPen());
		}
	}

	@Override
	public GDimension getPreferredSize() {
		// TODO: eliminate magic numbers
		return new GDimension() {

			@Override
			public int getWidth() {
				return (int) Math.round(((view.getApplication().getFontSize()
						* geoTextField.getFontSizeMultiplier()))
						* geoTextField.getLength() * TF_WIDTH_FACTOR);
			}

			@Override
			public int getHeight() {
				return (int) Math.round(((view.getApplication().getFontSize()
						* geoTextField.getFontSizeMultiplier()))
						* TF_HEIGHT_FACTOR) + TF_MARGIN;

			}
		};
	}

	@Override
	final public void draw(org.geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {
			if (isDrawingOnCanvas()) {
				drawOnCanvas(g2, geoTextField.getText());
			} else {
				if (geo.doHighlighting()) {
					label.setOpaque(true);
					label.setBackground(GColor.LIGHT_GRAY);

				} else {
					label.setOpaque(false);
				}
			}
		}
	}


	@Override
	protected void drawWidget(GGraphics2D g2) {
		boolean latexLabel = measureLabel(g2, geoTextField, labelDesc);
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
			drawLabel(g2, geoTextField, labelDesc);
		}

		String text = geoTextField.getText();
		// int truncIdx = geoTextField.getLinkedGeo() != null
		// ? (int) (boxWidth / (g2.getFont().getSize() * 0.5)) + 1
		// : Math.min(text.length(), geoTextField.getLength());

		GFont font = g2.getFont();
		g2.setFont(textFont.deriveFont(GFont.PLAIN));
		GPoint p = EuclidianStatic.drawIndexedString(view.getApplication(), g2,
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
		App.debug("[DrawTextFied] setFocus");

		textField.requestFocus();
		if (str != null && !str.equals("\t")) {
			textField.wrapSetText(str);
		}

	}

	/**
	 * @return label for this textfield
	 */
	public GLabel getLabel() {
		return label;
	}

	@Override
	protected void showWidget() {
		// App.debug("[CandvasDrawable] TextField show");
		textField.setVisible(true);
		if (!view.getEuclidianController().isTemporaryMode()) {
			textField.requestFocus();
		}
		box.setVisible(true);
	}

	@Override
	protected void hideWidget() {
		// App.debug("[CandvasDrawable] TextField hide");
		textField.setVisible(false);
		box.setVisible(false);
	}
}
