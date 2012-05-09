/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.common.euclidian;

import geogebra.common.awt.Color;
import geogebra.common.euclidian.event.ActionEvent;
import geogebra.common.euclidian.event.FocusEvent;
import geogebra.common.factories.AwtFactory;
import geogebra.common.factories.SwingFactory;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.Unicode;



/**
 * Checkbox for free GeoBoolean object.
 * 
 * @author Michael
 * @version
 */
public final class DrawComboBox extends Drawable implements RemoveNeeded {

	private final GeoList geoList;

	private boolean isVisible;

	private boolean hit = false;
	private String oldCaption;

	geogebra.common.javax.swing.AbstractJComboBox comboBox;
	geogebra.common.javax.swing.JLabel label;
	//ButtonListener bl;
	private ActionListener listener;
	private geogebra.common.javax.swing.Box box = SwingFactory.prototype.createHorizontalBox();

	public DrawComboBox(AbstractEuclidianView view, GeoList list) {
		this.view = view;
		this.geoList = list;
		geo = list;

		// action listener for checkBox
		//bl = new ButtonListener();
		comboBox = geoList.getComboBox(view.getViewID());
		label = SwingFactory.prototype.newJLabel("Label");
		//label.setLabelFor(comboBox);
		comboBox.setVisible(true);
		label.setVisible(true);
		
		comboBox.addActionListener(AwtFactory.prototype.newActionListener(new DrawComboBox.ActionListener()));

		//comboBox.addFocusListener(bl);
		
		//comboBox.addFocusListener(AwtFactory.prototype.newFocusListener(ifListener));
		//comboBox.addKeyListener(AwtFactory.prototype.newKeyListener(ifKeyListener));

		//label.addMouseListener(bl);
		//label.addMouseMotionListener(bl);
		box.add(label);
		box.add(comboBox);
		view.add(box);

		update();
	}

	
	/*
	private class ButtonListener implements  MouseListener, MouseMotionListener {

		private boolean dragging = false;
		private AbstractEuclidianController ec = view.getEuclidianController();

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
					&& view.getMode() == EuclidianView.MODE_MOVE) 
			{

				// make sure itemChanged does not change
				// the value back by faking a drag
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
		}

		public void mouseExited(MouseEvent arg0) {
			hit = false;
		}
		
		


	}*/


	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		// textField.setVisible(isVisible);
		// label.setVisible(isVisible);
		box.setVisible(isVisible);
		if (!isVisible) {
			return;
		}

		// don't need to worry about labeling options, just check if caption set or not
		if (geo.caption != null) {
			// get caption to show r
			String caption = geo.getCaption(StringTemplate.defaultTemplate);
			if (!caption.equals(oldCaption)) {
				oldCaption = caption;
				labelDesc = GeoElement.indicesToHTML(caption, true);
			}
			label.setText(labelDesc);
		} else {
			// make sure there's something to drag
			label.setText(Unicode.NBSP + Unicode.NBSP + Unicode.NBSP);
		}

		int fontSize = view.fontSize + geoList.getFontSize();
		AbstractApplication app = view.getApplication();

		geogebra.common.awt.Font vFont = view.getFont();
		geogebra.common.awt.Font font = app.getFontCanDisplay(comboBox.getItemAt(0).toString(), false, vFont.getStyle(), fontSize);

		//comboBox.setOpaque(true);
		label.setOpaque(false);
		comboBox.setFont(font);
		label.setFont(font);
		comboBox.setForeground(geo.getObjectColor());
		label.setForeground(geo.getObjectColor());
		geogebra.common.awt.Color bgCol = geo.getBackgroundColor();
		comboBox.setBackground(bgCol != null ? bgCol : view.getBackgroundCommon());

		comboBox.setFocusable(true);
		comboBox.setEditable(false);
		//updateText();

		box.validate();

		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;
		geogebra.common.awt.Dimension prefSize = box.getPreferredSize();
		labelRectangle.setBounds(xLabel, yLabel, prefSize.getWidth(), prefSize.getHeight());
		box.setBounds(labelRectangle);
	}

	private void updateLabel() {
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
		view.remove(box);
	}

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen coords)
	 */
	@Override
	final public boolean hit(int x, int y) {
		return box.getBounds().contains(x, y);
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

	/**
	 * Listens to events in this textfield
	 * @author Michael + Judit
	 */
	public class ActionListener extends geogebra.common.euclidian.event.ActionListener{


		/**
		 * @param e focus event
		 */
		public void actionPerformed(ActionEvent e) {
			
			//AbstractApplication.debug("action performed" + comboBox.getSelectedIndex());
			geoList.setSelectedIndex(comboBox.getSelectedIndex(), true);
		}
	
	}


}
