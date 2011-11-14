/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.euclidian;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.kernel.GeoButton;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoTextField;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.FunctionalNVar;
import geogebra.main.Application;
import geogebra.util.Unicode;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
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
import javax.swing.JTextField;


/**
 * Checkbox for free GeoBoolean object.
 * 
 * @author Michael
 * @version
 */
public final class DrawTextField extends Drawable {

	private GeoButton geoButton;

	private boolean isVisible;

	private boolean hit = false;
	private String oldCaption;

	JTextField textField;
	JLabel label;
	ButtonListener bl;
    Container box = Box.createHorizontalBox();


	public DrawTextField(EuclidianView view, GeoTextField geoButton) {
		this.view = view;
		this.geoButton = geoButton;
		geo = geoButton;

		// action listener for checkBox
		bl = new ButtonListener();
		textField = geoButton.getTextField();//new JTextField(20);	
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

		
		update();
	}

	private class ButtonListener implements 
			MouseListener, MouseMotionListener, FocusListener, KeyListener {

		private boolean dragging = false;
		private EuclidianController ec = view.getEuclidianController();

		/**
		 * Handles click on check box. Changes value of GeoBoolean.
		 */
		public void itemStateChanged(ItemEvent e) {
		}

		public void mouseDragged(MouseEvent e) {	
			
			dragging = true;			
			e.translatePoint(box.getX(), box.getY());
			ec.mouseDragged(e);
			view.setToolTipText(null);
		}

		public void mouseMoved(MouseEvent e) {			
			
			e.translatePoint(box.getX(), box.getY());
			ec.mouseMoved(e);
			view.setToolTipText(null);
		}

		public void mouseClicked(MouseEvent e) {
			
			if (e.getClickCount() > 1) return;
			
			e.translatePoint(box.getX(), box.getY());
			ec.mouseClicked(e);
		}

		public void mousePressed(MouseEvent e) {
			
			dragging = false;	
			e.translatePoint(box.getX(), box.getY());
			ec.mousePressed(e);		
		}

		public void mouseReleased(MouseEvent e) {	
			
			if (!dragging && !e.isMetaDown() && !e.isPopupTrigger()
					&& view.getMode() == EuclidianConstants.MODE_MOVE) 
			{
				// handle LEFT CLICK
				//geoBool.setValue(!geoBool.getBoolean());
				//geoBool.updateRepaint();
				//geo.runScript();
				//
				
				// make sure itemChanged does not change
		    	// the value back my faking a drag
		    	dragging = true;				
			}
			else {
				// handle right click and dragging
				e.translatePoint(box.getX(), box.getY());
				ec.mouseReleased(e);	
			}
			
		}

		public void mouseEntered(MouseEvent arg0) {
			hit = true;
			view.setToolTipText(null);
			updateText();
		}

		public void mouseExited(MouseEvent arg0) {
			hit = false;
		}

		public void focusGained(FocusEvent e) {
			view.getEuclidianController().textfieldHasFocus(true);
			updateText();
			
		}

		public void focusLost(FocusEvent e) {
			view.getEuclidianController().textfieldHasFocus(false);
			
			GeoElement linkedGeo = ((GeoTextField)geo).getLinkedGeo();
			
			
			if (linkedGeo != null) {

				String defineText = textField.getText();			
				
				if (linkedGeo.isGeoLine()) {
					String prefix = linkedGeo.getLabel() + ":";
					// need a: in front of 
					// X = (-0.69, 0) + \lambda (1, -2)
					if (!defineText.startsWith(prefix))
						defineText = prefix + defineText;
				} else if (linkedGeo.isGeoText()) {
					defineText = "\"" +  defineText + "\"";
				} else if (linkedGeo.isGeoPoint()) {
					if (((GeoPoint)linkedGeo).toStringMode == Kernel.COORD_COMPLEX) {
						// z=2 doesn't work for complex numbers (parses to GeoNumeric)
						defineText = defineText + "+0"+Unicode.IMAGINARY;
					}
				} else if (linkedGeo instanceof FunctionalNVar) {
					// string like f(x,y)=x^2
					// or f(\theta) = \theta
					defineText = linkedGeo.getLabel() + "(" + ((FunctionalNVar)linkedGeo).getVarString() + ")=" + defineText;
				} 
				
				try {
					linkedGeo = geo.getKernel().getAlgebraProcessor().changeGeoElementNoExceptionHandling(linkedGeo, defineText, false, true);
				} catch (Exception e1) {
					geo.getKernel().getApplication().showError(e1.getMessage());
					updateText();
					return;
				}			
				((GeoTextField)geo).setLinkedGeo(linkedGeo);
				
				updateText();

				
			}
			
			geo.runScripts(textField.getText());
			
		}

		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void keyReleased(KeyEvent e) {
			if (e.getKeyChar() == '\n') {
				//geo.runScripts(textField.getText());
				
				// this should be enough to trigger script event
				// ie in focusLost
				view.requestFocus();
			}
			
		}

		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		
	}
	
	private void updateText() {
		
		GeoElement linkedGeo = ((GeoTextField)geo).getLinkedGeo();
		if (linkedGeo != null) {
			
			String text;
			
			if (linkedGeo.isGeoText()) {
				text = ((GeoText)linkedGeo).getTextString();
			} else {
			
				// want just a number for eg a=3 but we want variables for eg y=m x + c
				boolean substituteNos = linkedGeo.isGeoNumeric() && linkedGeo.isIndependent();
				text = linkedGeo.getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, substituteNos);
			}
			
			if (linkedGeo.isGeoText() && text.indexOf("\n") > -1) {
				// replace linefeed with \\n
				while (text.indexOf("\n") > -1)
					text = text.replaceAll("\n", "\\\\\\\\n");
			}
			textField.setText(text);
			
		}
		
		((GeoTextField)geo).setText(textField.getText());

	}


	final public void update() {
		isVisible = geo.isEuclidianVisible();
		//textField.setVisible(isVisible);
		//label.setVisible(isVisible);
		box.setVisible(isVisible);
		if (!isVisible)
			return;		

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
			label.setText("");
		}			
		
		
		int fontSize = view.fontSize + geoButton.getFontSize();
		Application app = view.getApplication();
		
		Font vFont = view.getFont();
		Font font = app.getFontCanDisplay(textField.getText(), false, vFont.getStyle(), fontSize);
		
		textField.setOpaque(true);		
		label.setOpaque(false);		
		textField.setFont(font);				
		label.setFont(font);
		textField.setForeground(geo.getObjectColor());
		label.setForeground(geo.getObjectColor());
		Color bgCol = geo.getBackgroundColor();
		textField.setBackground(bgCol != null ? bgCol : view.getBackground());
		
		textField.setFocusable(true);
		textField.setEditable(true);
		updateText();
		// set checkbox state		
		//jButton.removeItemListener(bl);
		//jButton.setSelected(geo.getBoolean());
		//jButton.addItemListener(bl);
		
		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;		
		Dimension prefSize = box.getPreferredSize();
		labelRectangle.setBounds(xLabel, yLabel, prefSize.width,
				prefSize.height);
		box.setBounds(labelRectangle);	}

	private void updateLabel() {
		/*
		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;		

		labelRectangle.setBounds(xLabel, yLabel,
				 ((textSize == null) ? 0 : textSize.x),
				12);*/

	}

	final public void draw(Graphics2D g2) {
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
		view.remove(box);
	}
	
	/**
	 * was this object clicked at? (mouse pointer
	 * location (x,y) in screen coords)
	 */
	final public boolean hit(int x, int y) {
		return hit;				      
	}

	final public boolean isInside(Rectangle rect) {
		return rect.contains(labelRectangle);
	}

	/**
	 * Returns false
	 */
	public boolean hitLabel(int x, int y) {
		return false;
	}

	final public GeoElement getGeoElement() {
		return geo;
	}

	final public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}


}
