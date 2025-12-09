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

package org.geogebra.desktop.gui.view.algebra;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComboBox;

public abstract class MyComboBoxListener extends MouseAdapter
		implements ActionListener {

	@Override
	public void mousePressed(MouseEvent e) {
		Object src = e.getSource();

		doActionPerformed(src);
		if (src instanceof JComboBox) {
			JComboBox cb = (JComboBox) src;
			cb.setPopupVisible(false);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Object src = e.getSource();
		if (src instanceof JComboBox) {
			JComboBox cb = (JComboBox) src;
			cb.setPopupVisible(false);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		doActionPerformed(e.getSource());
	}

	/**
	 * Handle an event.
	 * @param source event source
	 */
	public abstract void doActionPerformed(Object source);
}