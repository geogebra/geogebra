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
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.euclidian.event.FocusEvent;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.GBox;
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
public final class DrawTextField extends Drawable implements RemoveNeeded {
	// TODO: examine these two, why are they needed and why these values.
	private static final double TF_HEIGHT_FACTOR = 1.22;
	private static final double TF_WIDTH_FACTOR = 0.83;

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
	private GBox box;

	private boolean drawOnCanvas;

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
		drawOnCanvas = view.getApplication().has(
				Feature.DRAW_FURNITURE_TO_CANVAS);
		box = geo.getKernel().getApplication().getSwingFactory()
				.createHorizontalBox(view.getEuclidianController());
		// action listener for checkBox
		// bl = new ButtonListener();
		ifListener = new InputFieldListener();
		ifKeyListener = new InputFieldKeyListener();
		textField = geoTextField.getTextField(view.getViewID(), this);// SwingFactory.prototype.newAutoCompleteTextField(geo.getLength(),
		// view.getApplication(),
		// this);
		// this will be set in update(): textField.showPopupSymbolButton(true);
		textField.setAutoComplete(false);
		textField.enableColoring(false);
		label = geo.getKernel().getApplication().getSwingFactory()
				.newJLabel("Label", false);

		// label.setLabelFor(textField); <- next row
		// textField.setLabel(label);

		textField.setVisible(true);
		label.setVisible(true);
		// ((geogebra.gui.inputfield.AutoCompleteTextField)
		// textField).addFocusListener(bl);
		textField.addFocusListener(AwtFactory.prototype
				.newFocusListener(ifListener));
		// label.addMouseListener(bl);
		// label.addMouseMotionListener(bl);
		textField.addKeyHandler(ifKeyListener);
		if (!drawOnCanvas) {
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
	public class InputFieldListener extends
			org.geogebra.common.euclidian.event.FocusListener {

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
			// geoTextField.updateText(textField);

			initialText = textField.getText();
		}

		/**
		 * @param e
		 *            focus event
		 */
		public void focusLost(FocusEvent e) {
			getView().getEuclidianController().textfieldHasFocus(false);

			// make sure (expensive) update doesn't happen unless needed
			// also caused problems when Object Properties opened
			if (!textField.getText().equals(initialText)) {
				geoTextField.textObjectUpdated(textField);
				geoTextField.textSubmitted();
				if (drawOnCanvas) {
					draw(view.getGraphicsForPen());
				}
			}

			if (drawOnCanvas) {
				textField.setVisible(false);

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
	public class InputFieldKeyListener implements
			org.geogebra.common.euclidian.event.KeyHandler {

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
				if (drawOnCanvas) {
					textField.setVisible(false);
					draw(view.getGraphicsForPen());
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

	// private class ButtonListener implements MouseListener,
	// MouseMotionListener,
	// FocusListener, KeyListener {
	//
	// private boolean dragging = false;
	// private final EuclidianController ec =
	// ((EuclidianView)view).getEuclidianController();
	//
	// public ButtonListener() {
	// // TODO Auto-generated constructor stub
	// }
	//
	// /**
	// * Handles click on check box. Changes value of GeoBoolean.
	// */
	// @SuppressWarnings("unused")
	// public void itemStateChanged(ItemEvent e) {
	// // TODO delete?
	// }
	//
	// public void mouseDragged(MouseEvent e) {
	//
	// dragging = true;
	// e.translatePoint(box.getX(), box.getY());
	// ec.mouseDragged(e);
	// ((EuclidianView)view).setToolTipText(null);
	// }
	//
	// public void mouseMoved(MouseEvent e) {
	//
	// e.translatePoint(box.getX(), box.getY());
	// ec.mouseMoved(e);
	// ((EuclidianView)view).setToolTipText(null);
	// }
	//
	// public void mouseClicked(MouseEvent e) {
	//
	// if (e.getClickCount() > 1) {
	// return;
	// }
	//
	// e.translatePoint(box.getX(), box.getY());
	// ec.mouseClicked(e);
	// }
	//

	// public void mousePressed(MouseEvent e) {
	//
	// // prevent textField editing on right click
	// if (Application.isRightClick(e)) {
	// e.consume();
	// }
	//
	// dragging = false;
	// e.translatePoint(box.getX(), box.getY());
	// ec.mousePressed(e);
	// }

	// public void mouseReleased(MouseEvent e) {
	//
	// // prevent textField editing on right click
	// if (Application.isRightClick(e)) {
	// e.consume();
	// }
	//
	// if (!dragging && !e.isMetaDown() && !e.isPopupTrigger()
	// && (view.getMode() == EuclidianConstants.MODE_MOVE)) {
	// // handle LEFT CLICK
	// // geoBool.setValue(!geoBool.getBoolean());
	// // geoBool.updateRepaint();
	// // geo.runScript();
	// //
	//
	// // make sure itemChanged does not change
	// // the value back my faking a drag
	// dragging = true;
	// } else {
	// // handle right click and dragging
	// e.translatePoint(box.getX(), box.getY());
	// ec.mouseReleased(e);
	// }
	//
	// }
	//
	// public void mouseEntered(MouseEvent arg0) {
	// if (!textField.hasFocus()) {
	// hit = true;
	// ((EuclidianView)view).setToolTipText(null);
	// geoButton.updateText(textField);
	// }
	// }
	// public void mouseExited(MouseEvent arg0) {
	// hit = false;
	// }
	//
	// public void focusGained(FocusEvent e) {
	// ((EuclidianView)view).getEuclidianController().textfieldHasFocus(true);
	// geoTextField.updateText(textField);
	//
	// }
	//
	// public void focusLost(FocusEvent e) {
	// ((EuclidianView)view).getEuclidianController().textfieldHasFocus(false);
	//
	// geoTextField.textObjectUpdated(textField);
	//
	// }
	//
	// public void keyPressed(KeyEvent e) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// public void keyReleased(KeyEvent e) {
	// if (e.getKeyChar() == '\n') {
	// ((EuclidianView)view).getEuclidianController().textfieldHasFocus(false);
	// geoTextField.textObjectUpdated(textField);
	// }
	//
	// }
	//
	// public void keyTyped(KeyEvent e) {
	// // TODO Auto-generated method stub
	//
	// }
	// }

	private int oldLength = 0;

	private GDimension prefSize;

	private GFont labelFont;

	private GPoint labelSize;

	private int labelFontSize;

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();

		textField.setVisible(drawOnCanvas ? false : isVisible);
		label.setVisible(drawOnCanvas ? false : isVisible);

		box.setVisible(drawOnCanvas ? false : isVisible);
		int length = geoTextField.getLength();
		if (length != oldLength) {
			textField.setColumns(length);
			textField
					.prepareShowSymbolButton(length > EuclidianConstants.SHOW_SYMBOLBUTTON_MINLENGTH);

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
			label.setText(labelDesc);
		} else {
			// put back to "" from "   " so that the position is the same in
			// ggb40 and ggb42
			label.setText("");
		}

		labelFontSize = (int) (view.getFontSize() * geoTextField
				.getFontSizeMultiplier());
		App app = view.getApplication();

		GFont vFont = view.getFont();
		GFont font = app.getFontCanDisplay(textField.getText(), false,
				vFont.getStyle(), labelFontSize);

		textField.setOpaque(true);
		label.setOpaque(false);
		textField.setFont(font);
		label.setFont(font);
		textField.setForeground(geo.getObjectColor());
		label.setForeground(geo.getObjectColor());
		GColor bgCol = geo.getBackgroundColor();
		textField.setBackground(bgCol != null ? bgCol : view
				.getBackgroundCommon());

		textField.setFocusable(true);
		textField.setEditable(true);
		geoTextField.updateText(textField);

		box.revalidate();

		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;

		prefSize = box.getPreferredSize();

		labelRectangle.setBounds(xLabel, yLabel, prefSize.getWidth(),
				prefSize.getHeight());
			box.setBounds(labelRectangle);
		if (!drawOnCanvas) {
			draw(view.getGraphicsForPen());
		}
	}

	final public GDimension getPrefSize() {
		// TODO: eliminate magic numbers
		return new GDimension() {

			@Override
			public int getWidth() {
				return (int) Math
						.round(((view.getApplication().getFontSize() * geoTextField
						.getFontSizeMultiplier()))
								* geoTextField.getLength() * TF_WIDTH_FACTOR
);
			}

			@Override
			public int getHeight() {
				return (int) Math
						.round(((view.getApplication().getFontSize() * geoTextField
								.getFontSizeMultiplier())) * TF_HEIGHT_FACTOR)
						+ TF_MARGIN;

			}
		};
	}
	@Override
	final public void draw(org.geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {
			if (drawOnCanvas) {

				drawOnCanvas(g2);
			}
			else {
				if (geo.doHighlighting()) {
					label.setOpaque(true);
					label.setBackground(GColor.LIGHT_GRAY);

				} else {
					label.setOpaque(false);
				}
			}
		}
	}

	final public void drawOnCanvas(org.geogebra.common.awt.GGraphics2D g2) {
		App app = view.getApplication();
		prefSize = getPrefSize();

		GFont vFont = view.getFont();
		labelFont = app.getFontCanDisplay(textField.getText(), false,
				vFont.getStyle(), labelFontSize);

		g2.setFont(labelFont);
		g2.setStroke(EuclidianStatic.getDefaultStroke());

		g2.setPaint(geo.getObjectColor());

		if (geo.isLabelVisible()) {
			drawTextFieldLabel(g2);
			drawTextField(g2);


		}

	}


	private void drawTextField(GGraphics2D g2) {
		int inputLeft = xLabel + labelSize.x + 2;
		GColor bgColor = geo.getBackgroundColor();
		g2.setPaint(bgColor != null ? bgColor : view.getBackgroundCommon());
		g2.fillRoundRect(inputLeft, yLabel, prefSize.getWidth(), prefSize.getHeight(),
				5, 5);

		g2.setPaint(geo.getObjectColor());
		// g2.drawRoundRect(inputLeft, yLabel, prefSize.getWidth(),
		// prefSize.getHeight(),
		// 5, 5);
		//
		// g2.setPaint(GColor.RED);

		int y = getTextY();
		labelSize = EuclidianStatic.drawIndexedString(view.getApplication(),
				g2, labelDesc, xLabel, yLabel + y, false, false);

		if (geo.doHighlighting()) {
			g2.setPaint(GColor.LIGHT_GRAY);

			/* some magic */
			g2.fillRect(xLabel, yLabel, labelSize.x,
					(int) Math.round(labelFontSize * 1.5));

			g2.setPaint(geo.getObjectColor());
			labelSize = EuclidianStatic.drawIndexedString(
					view.getApplication(), g2, labelDesc, xLabel, yLabel + y,
					false, false);

		}


		EuclidianStatic.drawIndexedString(view.getApplication(), g2,
				geoTextField.getText(), xLabel + labelSize.x + 2, yLabel + y
						- 1,
				false, false);

		g2.setPaint(GColor.LIGHT_GRAY);
		g2.drawRoundRect(inputLeft, yLabel, prefSize.getWidth(),
				prefSize.getHeight(), 5, 5);
		labelRectangle.setBounds(inputLeft, yLabel,
				prefSize.getWidth(),
 prefSize.getHeight());
		box.setBounds(labelRectangle);
		GRectangle r = box.getBounds();
	}

	private int getTextY() {
		return prefSize.getHeight() / 2 + (labelFontSize / 2) - 4;
	}

	private void drawTextFieldLabel(GGraphics2D g2) {

		g2.setPaint(geo.getObjectColor());
		labelSize = EuclidianStatic.drawIndexedString(view.getApplication(),
				g2, labelDesc, xLabel, yLabel + getTextY(), false,
				false);
	}

	/**
	 * Removes button from view again
	 */
	final public void remove() {
		view.remove(box);
	}

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */
	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		int left = xLabel;
		int top = yLabel;
		int right = left + prefSize.getWidth();
		int bottom = top + prefSize.getHeight();

		// App.debug(x + ", " + y + ": (" + left + ", " + top + ", " + right
		// + ", " + bottom + ")");


		boolean res = x > left && x < right && y > top && y < bottom;
		// App.debug("[DoC] box.getBounds().contains(x, y) " + res);
		return res;
	}

	public void showIntputField(boolean show) {
		if (show) {
			textField.setVisible(true);
		} else {
			textField.setVisible(false);
		}
		box.setVisible(show);
	}
	@Override
	final public boolean isInside(org.geogebra.common.awt.GRectangle rect) {
		return rect.contains(labelRectangle);
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return box.getBounds().intersects(rect);
	}

	/**
	 * Returns false
	 */
	@Override
	public boolean hitLabel(int x, int y) {
		return false;
	}

	@Override
	final public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	final public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	/**
	 * @param str
	 *            input string
	 */
	public void setFocus(final String str) {
		textField.requestFocus();
		if (str != null && !str.equals("\t")) {
			// SwingUtilities.invokeLater(new Runnable() {
			// public void run() {
			// textField.setText(str);
			// }
			// });
			textField.wrapSetText(str);
		}

	}

	/**
	 * @return label for this textfield
	 */
	public GLabel getLabel() {
		return label;
	}
}
