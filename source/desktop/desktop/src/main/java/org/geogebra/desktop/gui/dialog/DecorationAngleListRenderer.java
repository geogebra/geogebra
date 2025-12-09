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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.awt.GGraphics2DD;

/**
 * @author Loic Le Coq date 31/10/2006 This class defines the renderer for the
 *         ComboBox where the user chooses the decoration for GeoAngle
 * 
 * 
 * 
 */

public class DecorationAngleListRenderer extends JPanel
		implements ListCellRenderer {
	private static final long serialVersionUID = 1L;
	private Line2D.Double tick = new Line2D.Double();
	private Arc2D.Double arc = new Arc2D.Double();
	private GeneralPath polygon = new GeneralPath();
	private int id = 0;

	public DecorationAngleListRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		// Get the selected index. (The index param isn't
		// always valid, so just use the value.)
		int selectedIndex = ((Integer) value).intValue();
		this.id = selectedIndex;
		if (isSelected) {
			setBackground(Color.LIGHT_GRAY);
			// setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			// setForeground(list.getForeground());
		}
		setBorder(BorderFactory.createEmptyBorder(12, 2, 12, 2));
		return this;
	}

	private void drawTick(double angle) {
		tick.setLine(13 + 37 * Math.cos(angle), 27 - 37 * Math.sin(angle),
				13 + 43 * Math.cos(angle), 27 - 43 * Math.sin(angle));
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		GGraphics2DD.setAntialiasing(g2);

		// g2.setColor(getBackground());
		if (getBackground() == Color.LIGHT_GRAY) {
			g2.setColor(Color.LIGHT_GRAY);
		} else {
			g2.setColor(Color.WHITE);
		}

		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(Color.BLACK);
		g2.drawLine(13, 27, 67, 27);
		g2.drawLine(13, 27, 67, 3);
		arc.setArcByCenter(13, 27, 40, 0, 24, Arc2D.OPEN);
		g2.draw(arc);
		switch (id) {
		default:
			// do nothing
			break;
		case GeoElement.DECORATION_ANGLE_TWO_ARCS:
			arc.setArcByCenter(13, 27, 35, 0, 24, Arc2D.OPEN);
			g2.draw(arc);
			break;
		case GeoElement.DECORATION_ANGLE_THREE_ARCS:
			arc.setArcByCenter(13, 27, 35, 0, 24, Arc2D.OPEN);
			g2.draw(arc);
			arc.setArcByCenter(13, 27, 45, 0, 24, Arc2D.OPEN);
			g2.draw(arc);
			break;
		case GeoElement.DECORATION_ANGLE_ONE_TICK:
			drawTick(Math.toRadians(12));
			g2.draw(tick);
			break;
		case GeoElement.DECORATION_ANGLE_TWO_TICKS:
			drawTick(Math.toRadians(9.6));
			g2.draw(tick);
			drawTick(Math.toRadians(14.4));
			g2.draw(tick);
			break;
		case GeoElement.DECORATION_ANGLE_THREE_TICKS:
			drawTick(Math.toRadians(12));
			g2.draw(tick);
			drawTick(Math.toRadians(7));
			g2.draw(tick);
			drawTick(Math.toRadians(16));
			g2.draw(tick);
			break;
		case GeoElement.DECORATION_ANGLE_ARROW_ANTICLOCKWISE:
			polygon.reset();
			polygon.moveTo(56, 15);
			polygon.lineTo(48, 19);
			polygon.lineTo(50, 10);
			polygon.lineTo(56, 15);
			polygon.closePath();
			g2.fill(polygon);
			break;
		case GeoElement.DECORATION_ANGLE_ARROW_CLOCKWISE:
			polygon.reset();
			polygon.moveTo(54, 27);
			polygon.lineTo(48, 20);
			polygon.lineTo(56, 18);
			polygon.lineTo(54, 27);
			polygon.closePath();
			g2.fill(polygon);
			break;
		}
	}

}
