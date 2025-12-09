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

package org.geogebra.desktop.gui.dialog.options;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.geogebra.desktop.main.AppD;

/**
 * 
 * Make sure eg Malayalam is displayed in the correct font (characters not in
 * default Java font)
 * 
 * @author michael
 *
 */
public class LanguageRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;

	private AppD app;

	LanguageRenderer(AppD app) {
		super();
		this.app = app;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Component ret = super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);

		if (value instanceof String) {
			String language = (String) value;
			ret.setFont(app.getFontCanDisplayAwt(language));
		}
		return ret;

	}

}
