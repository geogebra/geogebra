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

package org.geogebra.desktop.gui.util;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Custom toggle button for use in stylebars
 * 
 * @author G. Sturr
 *
 */
public class ToggleButtonD extends JButton {

	private static final long serialVersionUID = 1L;
	private int myHeight;

	/**
	 * @param icon icon
	 * @param height height
	 */
	public ToggleButtonD(ImageIcon icon, int height) {
		super(icon);
		initButton(height);

		Dimension d = new Dimension(icon.getIconWidth(), height);
		setIcon(GeoGebraIconD.ensureIconSize(icon, d));
	}

	/**
	 * @param height height in pixels
	 */
	public ToggleButtonD(int height) {
		super();
		initButton(height);
	}

	private void initButton(int height) {

		this.myHeight = height;
		this.setRolloverEnabled(true);

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				toggle();
			}
		});
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		Dimension d = this.getPreferredSize();
		d.height = myHeight;
		this.setPreferredSize(d);
	}

	/**
	 * Update the state of the button
	 * @param geos selected elements
	 */
	public void update(List<GeoElement> geos) {
		// override
	}

	protected void toggle() {
		this.setSelected(!this.isSelected());
	}
}
