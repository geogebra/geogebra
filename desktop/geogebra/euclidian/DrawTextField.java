/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.euclidian;

import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.RemoveNeeded;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.gui.inputfield.AutoCompleteTextField;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * Checkbox for free GeoBoolean object.
 * 
 * @author Michael
 * @version
 */
public final class DrawTextField extends Drawable implements RemoveNeeded {

	final GeoTextField geoButton;

	private boolean isVisible;

	boolean hit = false;
	private String oldCaption;

	AutoCompleteTextField textField;
	JLabel label;
	ButtonListener bl;
	Container box = Box.createHorizontalBox();

	public DrawTextField(EuclidianView view, GeoTextField geo) {
		this.view = view;
		this.geoButton = geo;
		this.geo = geo;

		// action listener for checkBox
		bl = new ButtonListener();
		textField = new AutoCompleteTextField(geo.getLength(), view.getApplication());
		textField.showPopupSymbolButton(true);
		textField.setAutoComplete(false);
		textField.enableColoring(false);
		label = new JLabel("Label");
		label.setLabelFor(textField);
		textField.setVisible(true);
		label.setVisible(true);
		textField.addFocusListener(bl);
		label.addMouseListener(bl);
		label.addMouseMotionListener(bl);
		textField.addKeyListener(bl);
		box.add(label);
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

	private class ButtonListener implements MouseListener, MouseMotionListener,
			FocusListener, KeyListener {

		private boolean dragging = false;
		private final EuclidianController ec = ((EuclidianView)view).getEuclidianController();

		public ButtonListener() {
			// TODO Auto-generated constructor stub
		}

		/**
		 * Handles click on check box. Changes value of GeoBoolean.
		 */
		@SuppressWarnings("unused")
		public void itemStateChanged(ItemEvent e) {
			// TODO delete?
		}

		public void mouseDragged(MouseEvent e) {

			dragging = true;
			e.translatePoint(box.getX(), box.getY());
			ec.mouseDragged(e);
			((EuclidianView)view).setToolTipText(null);
		}

		public void mouseMoved(MouseEvent e) {

			e.translatePoint(box.getX(), box.getY());
			ec.mouseMoved(e);
			((EuclidianView)view).setToolTipText(null);
		}

		public void mouseClicked(MouseEvent e) {

			if (e.getClickCount() > 1) {
				return;
			}

			e.translatePoint(box.getX(), box.getY());
			ec.mouseClicked(e);
		}

		public void mousePressed(MouseEvent e) {

			// prevent textField editing on right click
			if (Application.isRightClick(e)) {
				e.consume();
			}

			dragging = false;
			e.translatePoint(box.getX(), box.getY());
			ec.mousePressed(e);
		}

		public void mouseReleased(MouseEvent e) {

			// prevent textField editing on right click
			if (Application.isRightClick(e)) {
				e.consume();
			}

			if (!dragging && !e.isMetaDown() && !e.isPopupTrigger()
					&& (view.getMode() == EuclidianConstants.MODE_MOVE)) {
				// handle LEFT CLICK
				// geoBool.setValue(!geoBool.getBoolean());
				// geoBool.updateRepaint();
				// geo.runScript();
				//

				// make sure itemChanged does not change
				// the value back my faking a drag
				dragging = true;
			} else {
				// handle right click and dragging
				e.translatePoint(box.getX(), box.getY());
				ec.mouseReleased(e);
			}

		}

		public void mouseEntered(MouseEvent arg0) {
			if (!textField.hasFocus()) {
				hit = true;
				((EuclidianView)view).setToolTipText(null);
				geoButton.updateText(textField);
			}
		}
		public void mouseExited(MouseEvent arg0) {
			hit = false;
		}

		public void focusGained(FocusEvent e) {
			((EuclidianView)view).getEuclidianController().textfieldHasFocus(true);
			geoButton.updateText(textField);

		}

		public void focusLost(FocusEvent e) {
			((EuclidianView)view).getEuclidianController().textfieldHasFocus(false);

			geoButton.textObjectUpdated(textField);

		}

		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		public void keyReleased(KeyEvent e) {
			if (e.getKeyChar() == '\n') {
				((EuclidianView)view).getEuclidianController().textfieldHasFocus(false);
				geoButton.textObjectUpdated(textField);
			}

		}

		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub

		}

	}


	private int oldLength = 0;
	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		// textField.setVisible(isVisible);
		// label.setVisible(isVisible);
		box.setVisible(isVisible);
		int length = geoButton.getLength();
		if(length!=oldLength){
			textField.setColumns(length);
			textField.showPopupSymbolButton(length>8);
		}
		if (!isVisible) {
			return;
		}

		// show hide label by setting text
		if (geo.isLabelVisible()) {
			// get caption to show r
			String caption = geo.getCaption();
			if (!caption.equals(oldCaption)) {
				oldCaption = caption;
				labelDesc = GeoElement.indicesToHTML(caption, true);
			}
			box.setVisible(false); // avoid redraw error
			label.setText(labelDesc);
			box.setVisible(true);
		} else {
			//spaces make the textfield draggable even if label is invisible
			label.setText("   ");
		}

		int fontSize = view.getFontSize() + geoButton.getFontSize();
		Application app = ((EuclidianView)view).getApplication();

		Font vFont = geogebra.awt.Font.getAwtFont(view.getFont());
		Font font = app.getFontCanDisplayAwt(textField.getText(), false,
				vFont.getStyle(), fontSize);

		textField.setOpaque(true);
		label.setOpaque(false);
		textField.setFont(font);
		label.setFont(font);
		textField.setForeground(geogebra.awt.Color.getAwtColor(geo
				.getObjectColor()));
		label.setForeground(geogebra.awt.Color.getAwtColor(geo.getObjectColor()));
		Color bgCol = geogebra.awt.Color.getAwtColor(geo.getBackgroundColor());
		textField.setBackground(bgCol != null ? bgCol : ((EuclidianView)view).getBackground());

		textField.setFocusable(true);
		textField.setEditable(true);
		geoButton.updateText(textField);
		// set checkbox state
		// jButton.removeItemListener(bl);
		// jButton.setSelected(geo.getBoolean());
		// jButton.addItemListener(bl);

		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;
		Dimension prefSize = box.getPreferredSize();
		labelRectangle.setBounds(xLabel, yLabel, prefSize.width,
				prefSize.height);
		box.setBounds(geogebra.awt.Rectangle.getAWTRectangle(labelRectangle));
	}

	@SuppressWarnings("unused")
	private void updateLabel() {
		// TODO delete?
		/*
		 * xLabel = geo.labelOffsetX; yLabel = geo.labelOffsetY;
		 * 
		 * labelRectangle.setBounds(xLabel, yLabel, ((textSize == null) ? 0 :
		 * textSize.x), 12);
		 */

	}

	@Override
	final public void draw(geogebra.common.awt.Graphics2D g2) {
		if (isVisible) {
			if (geo.doHighlighting()) {
				label.setOpaque(true);
				label.setBackground(Color.lightGray);

			} else {
				label.setOpaque(false);
			}
		}
	}

	/**
	 * Removes button from view again
	 */
	final public void remove() {
		((EuclidianView)view).remove(box);
	}

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */
	@Override
	final public boolean hit(int x, int y) {
		return hit;
	}

	@Override
	final public boolean isInside(geogebra.common.awt.Rectangle rect) {
		return rect.contains(labelRectangle);
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

	public void setFocus(final String str) {
		textField.requestFocus();
		if (str != null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					textField.setText(str);
				}
			});
		}
		
	}

}
