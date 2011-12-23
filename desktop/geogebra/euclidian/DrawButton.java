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
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.main.Application;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import geogebra.common.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;


/**
 * Checkbox for free GeoBoolean object.
 * 
 * @author Markus Hohenwarter
 * @version
 */
public final class DrawButton extends Drawable {

	private GeoButton geoButton;

	private boolean isVisible;

	boolean hit = false;
	private String oldCaption;

	private Point textSize = new Point(0,0);
	
	MyButton myButton;
	ButtonListener bl;


	public DrawButton(EuclidianView view, GeoButton geoButton) {
		this.view = view;
		this.geoButton = geoButton;
		geo = geoButton;

		// action listener for checkBox
		bl = new ButtonListener();
		myButton = new MyButton(geoButton, view);	
		myButton.addItemListener(bl);
		myButton.addMouseListener(bl);
		myButton.addMouseMotionListener(bl);
		myButton.setFocusable(false);	
		view.add(myButton);
		
		update();
	}

	private class ButtonListener implements ItemListener,
			MouseListener, MouseMotionListener {

		private boolean dragging = false;
		private EuclidianController ec = view.getEuclidianController();

		/**
		 * Handles click on check box. Changes value of GeoBoolean.
		 */
		public void itemStateChanged(ItemEvent e) {
		}

		public void mouseDragged(MouseEvent e) {	
			dragging = true;			
			e.translatePoint(myButton.getX(), myButton.getY());
			ec.mouseDragged(e);
			view.setToolTipText(null);
		}

		public void mouseMoved(MouseEvent e) {				
			e.translatePoint(myButton.getX(), myButton.getY());
			ec.mouseMoved(e);
			view.setToolTipText(null);
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) return;
			
			e.translatePoint(myButton.getX(), myButton.getY());
			ec.mouseClicked(e);
		}

		public void mousePressed(MouseEvent e) {
			dragging = false;	
			e.translatePoint(myButton.getX(), myButton.getY());
			ec.mousePressed(e);		
		}

		public void mouseReleased(MouseEvent e) {	
			if (!dragging && !e.isMetaDown() && !e.isPopupTrigger()
					&& view.getMode() == EuclidianConstants.MODE_MOVE) 
			{
				// handle LEFT CLICK
				//geoBool.setValue(!geoBool.getBoolean());
				//geoBool.updateRepaint();
				
				// delayed run to allow time for focus leaving event 
				// to be triggered for GeoTextField
	            SwingUtilities.invokeLater( new Runnable(){ public void
	            	run() { geo.runScripts(null); }});

				
				// make sure itemChanged does not change
		    	// the value back my faking a drag
		    	dragging = true;				
			}
			else {
				// handle right click and dragging
				e.translatePoint(myButton.getX(), myButton.getY());
				ec.mouseReleased(e);	
			}
		}

		public void mouseEntered(MouseEvent arg0) {
			hit = true;
			view.setToolTipText(null);
		}

		public void mouseExited(MouseEvent arg0) {
			hit = false;
		}		
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		myButton.setVisible(isVisible);
		if (!isVisible)
			return;

		// get caption to show r
		String caption = geo.getCaption();
		if (!caption.equals(oldCaption)) {
			oldCaption = caption;
			labelDesc = GeoElement.indicesToHTML(caption, true);
		}
		myButton.setText(labelDesc);

		int fontSize = view.getFontSize() + geoButton.getFontSize();
		Application app = view.getApplication();

		myButton.setOpaque(true);
		myButton.setFont(app.getFontCanDisplay(myButton.getText(),
				geoButton.isSerifFont(), geoButton.getFontStyle(), fontSize));

		// myButton.setForeground(geogebra.awt.Color.getAwtColor(geo.getObjectColor()));
		// Color bgCol = geo.getBackgroundColor();
		// myButton.setBackground(Color.red);//bgCol != null ? bgCol :
		// view.getBackground());
		// set checkbox state
		// jButton.removeItemListener(bl);
		// jButton.setSelected(geo.getBoolean());
		// jButton.addItemListener(bl);

		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;
		Dimension prefSize = myButton.getPreferredSize();
		labelRectangle.setBounds(xLabel, yLabel, prefSize.width,
				prefSize.height);
		myButton.setBounds(geogebra.awt.Rectangle.getAWTRectangle(labelRectangle));
	}

	private void updateLabel() {
		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;

		labelRectangle.setBounds(xLabel, yLabel, ((textSize == null) ? 0
				: textSize.x), 12);
	}

	@Override
	final public void draw(Graphics2D g2) {

		if (isVisible) {		
			myButton.setSelected(geo.doHighlighting());
			// setSelected doesn't seem to do anything in Windows XP
			//if (!Application.MAC_OS) {
			//	// but looks ugly in MacOS, see #820
			//	button.setBackground(geo.doHighlighting() ? Color.blue : Color.white);
			//}
		}
	}

	/**
	 * Removes button from view again
	 */
	final public void remove() {
		view.remove(myButton);
	}
	
	/**
	 * was this object clicked at? (mouse pointer
	 * location (x,y) in screen coords)
	 */
	@Override
	final public boolean hit(int x, int y) {
		return hit;				      
	}

	@Override
	final public boolean isInside(Rectangle rect) {
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

}
