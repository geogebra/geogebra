/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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
import org.geogebra.desktop.awt.AwtFactoryD;

/**
 * used by LineStylePanel for rendering a combobox with different line styles
 * (dashing)
 */
public class DashListRenderer extends JPanel implements ListCellRenderer<Integer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// map with (type, dashStrokes for type) pairs
	private HashMap<Integer, BasicStroke> dashStrokeMap;
	private BasicStroke dashStroke;
	// private Color bgColor;
	private boolean nullValue = false;

	/**
	 * Dash styles renderer
	 */
	public DashListRenderer() {
		// init stroke map
		dashStrokeMap = new HashMap<>();
		int type;
		BasicStroke stroke;
		for (int i = 0; i < EuclidianView.getLineTypeLength(); i++) {
			type = EuclidianView.getLineType(i);
			stroke = AwtFactoryD.getAwtStroke(EuclidianStatic.getStroke(1.0f, type));
			dashStrokeMap.put(type, stroke);
		}
	}

	@Override
	public Component getListCellRendererComponent(JList list, Integer value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			// setBackground(list.getSelectionBackground());
			setBackground(Color.LIGHT_GRAY);
		} else {
			setBackground(list.getBackground());
		}

		nullValue = value == null;
		if (nullValue) {
			return this;
		}

		// value is an Integer with the line type's int value
		int type = value;
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