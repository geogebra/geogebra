/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.common.euclidian.draw;

import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.MyButton;
import geogebra.common.euclidian.RemoveNeeded;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;


/**
 * Button (for scripting)
 * 
 * @author Markus Hohenwarter
 */
public final class DrawButton extends Drawable implements RemoveNeeded {

	private GeoButton geoButton;

	private boolean isVisible;

	private String oldCaption;
	/** button "component" */
	public MyButton myButton;


	/**
	 * @param view view
	 * @param geoButton button
	 */
	public DrawButton(EuclidianView view, GeoButton geoButton) {
		this.view = view;
		this.geoButton = geoButton;
		geo = geoButton;
		myButton = new MyButton(geoButton, view);
		// action listener for checkBox
		/*bl = new ButtonListener();
			
		myButton.addItemListener(bl);
		myButton.addMouseListener(bl);
		myButton.addMouseMotionListener(bl);
		myButton.setFocusable(false);*/	
		//view.add(myButton);
		
		update();
	}

//	private class ButtonListener implements ItemListener,
//			MouseListener, MouseMotionListener {
//
//		private boolean dragging = false;
//		private EuclidianController ec = ((EuclidianView)view).getEuclidianController();
//
//		/**
//		 * Handles click on check box. Changes value of GeoBoolean.
//		 */
//		public void itemStateChanged(ItemEvent e) {
//		}
//
//		public void mouseDragged(MouseEvent e) {	
//			dragging = true;			
//			e.translatePoint(myButton.getX(), myButton.getY());
//			ec.mouseDragged(e);
//			((EuclidianView)view).setToolTipText(null);
//		}
//
//		public void mouseMoved(MouseEvent e) {				
//			e.translatePoint(myButton.getX(), myButton.getY());
//			ec.mouseMoved(e);
//			((EuclidianView)view).setToolTipText(null);
//		}
//
//		public void mouseClicked(MouseEvent e) {
//			if (e.getClickCount() > 1) return;
//			
//			e.translatePoint(myButton.getX(), myButton.getY());
//			ec.mouseClicked(e);
//		}
//
//		public void mousePressed(MouseEvent e) {
//			dragging = false;	
//			e.translatePoint(myButton.getX(), myButton.getY());
//			ec.mousePressed(e);		
//		}
//
//		public void mouseReleased(MouseEvent e) {	
//			if (!dragging && !e.isMetaDown() && !e.isPopupTrigger()
//					&& view.getMode() == EuclidianConstants.MODE_MOVE) 
//			{
//				// handle LEFT CLICK
//				//geoBool.setValue(!geoBool.getBoolean());
//				//geoBool.updateRepaint();
//				
//				// delayed run to allow time for focus leaving event 
//				// to be triggered for GeoTextField
//	            SwingUtilities.invokeLater( new Runnable(){ public void
//	            	run() { geo.runScripts(null); }});
//
//				
//				// make sure itemChanged does not change
//		    	// the value back my faking a drag
//		    	dragging = true;				
//			}
//			else {
//				// handle right click and dragging
//				e.translatePoint(myButton.getX(), myButton.getY());
//				ec.mouseReleased(e);	
//			}
//		}
//
//		public void mouseEntered(MouseEvent arg0) {
//			hit = true;
//			((EuclidianView)view).setToolTipText(null);
//		}
//
//		public void mouseExited(MouseEvent arg0) {
//			hit = false;
//		}		
//	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;

		// get caption to show r
		String caption = geo.getCaption(StringTemplate.defaultTemplate);
		if (!caption.equals(oldCaption)) {
			oldCaption = caption;
			labelDesc = GeoElement.indicesToHTML(caption, true);
		}
		myButton.setText(labelDesc);

		int fontSize = (int) (view.getFontSize() * geoButton.getFontSizeMultiplier());
		App app = view.getApplication();

		//myButton.setOpaque(true);
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
		
		labelRectangle.setBounds(xLabel, yLabel, myButton.getWidth(),
				myButton.getHeight());
		myButton.setBounds(labelRectangle);
	}

	@Override
	final public void draw(geogebra.common.awt.GGraphics2D g2) {

		if (isVisible) {		
			myButton.setSelected(geo.doHighlighting());
			myButton.paintComponent(g2);
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
		//((EuclidianView)view).remove(myButton);
	}
	
	/**
	 * was this object clicked at? (mouse pointer
	 * location (x,y) in screen coords)
	 */
	@Override
	final public boolean hit(int x, int y) {
		return myButton.getBounds().contains(x, y) && isVisible;				      
	}

	@Override
	final public boolean isInside(GRectangle rect) {
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
