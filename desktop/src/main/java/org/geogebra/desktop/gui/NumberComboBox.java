/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui;

import java.awt.Dimension;

import javax.swing.JComboBox;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.desktop.main.AppD;

import com.himamis.retex.editor.share.util.Unicode;

public class NumberComboBox extends JComboBox<String> {

	private static final long serialVersionUID = 1L;

	private static final int MAX_FRAC_DIGITS = 5;
	private NumberFormatAdapter nf;
	private Kernel kernel;

	public NumberComboBox(final AppD app) {
		this(app, true);
	}

	/**
	 * @param app application
	 * @param prefill whether to prefil with default values
	 */
	public NumberComboBox(final AppD app, boolean prefill) {
		kernel = app.getKernel();
		if (prefill) {
			addItem("1");
			addItem(Unicode.PI_STRING);
			addItem(Unicode.PI_HALF_STRING);
		}
		setEditable(true);
		setSelectedItem(null);

		nf = FormatFactory.getPrototype().getNumberFormat(MAX_FRAC_DIGITS);
		// nf.setGroupingUsed(false);
		// nf.setMaximumFractionDigits(MAX_FRAC_DIGITS);

		final Dimension dim = getPreferredSize();
		dim.width = app.getPlainFont().getSize() * (MAX_FRAC_DIGITS + 3);
		setPreferredSize(dim);
	}

	/**
	 * @param val value
	 */
	public void setValue(final double val) {
		setSelectedItem(
				kernel.formatPiE(val, nf, StringTemplate.defaultTemplate));
	}

	/**
	 * @return text value (trimmed)
	 */
	public String getValue() {
		final Object ob = getSelectedItem();
		if (ob == null) {
			return "";
		}

		return ob.toString().trim();
	}

}
