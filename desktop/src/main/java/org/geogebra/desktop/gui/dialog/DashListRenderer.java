/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.dialog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.desktop.factories.AwtFactoryD;

/**
 * used by LineStylePanel for rendering a combobox with different line styles
 * (dashing)
 */
public class DashListRenderer extends JPanel implements ListCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// map with (type, dashStrokes for type) pairs
	private HashMap<Integer, BasicStroke> dashStrokeMap;
	private BasicStroke dashStroke;
	// private Color bgColor;
	private boolean nullValue = false;

	public DashListRenderer() {
		// init stroke map
		dashStrokeMap = new HashMap<Integer, BasicStroke>();
		int type;
		BasicStroke stroke;
		for (int i = 0; i < EuclidianView.getLineTypeLength(); i++) {
			type = EuclidianView.getLineType(i);
			stroke = ((AwtFactoryD) AwtFactory.getPrototype())
					.getAwtStroke(EuclidianStatic
					.getStroke(1.0f, type));
			dashStrokeMap.put(type, stroke);
		}
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (isSelected)
			// setBackground(list.getSelectionBackground());
			setBackground(Color.LIGHT_GRAY);
		else
			setBackground(list.getBackground());

		nullValue = value == null;
		if (nullValue)
			return this;

		// value is an Integer with the line type's int value
		int type = ((Integer) value).intValue();
		// get the dashpanel for this dashing type
		dashStroke = dashStrokeMap.get(type);
		return this;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// clear background
		// g2.setColor(getBackground());
		if (getBackground() == Color.LIGHT_GRAY) {
			g2.setColor(Color.LIGHT_GRAY);
		} else {
			g2.setColor(Color.WHITE);
		}
		// g2.clearRect(0, 0, getWidth(), getHeight());
		g.fillRect(0, 0, getWidth(), getHeight());
		if (nullValue) {
			return;
		}

		// draw dashed line
		g2.setPaint(Color.black);
		g2.setStroke(dashStroke);
		int mid = getHeight() / 2;
		g2.drawLine(0, mid, getWidth(), mid);
	}
} // DashListRenderer