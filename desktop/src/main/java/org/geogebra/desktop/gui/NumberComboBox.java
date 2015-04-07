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

public class NumberComboBox extends JComboBox {

	private static final long serialVersionUID = 1L;

	private static final int MAX_FRAC_DIGITS = 5;
	private static final String PI_STRING = "\u03c0";

	private NumberFormatAdapter nf;
	private Kernel kernel;

	public NumberComboBox(final AppD app) {
		kernel = app.getKernel();

		addItem("1"); // pi
		addItem(PI_STRING); // pi
		addItem(PI_STRING + "/2"); // pi/2
		setEditable(true);
		setSelectedItem(null);

		nf = FormatFactory.prototype.getNumberFormat(MAX_FRAC_DIGITS);
		// nf.setGroupingUsed(false);
		// nf.setMaximumFractionDigits(MAX_FRAC_DIGITS);

		final Dimension dim = getPreferredSize();
		dim.width = app.getPlainFont().getSize() * (MAX_FRAC_DIGITS + 3);
		setPreferredSize(dim);
	}

	public void setValue(final double val) {
		setSelectedItem(kernel.formatPiE(val, nf,
				StringTemplate.defaultTemplate));
	}

	public double getValue() {
		final Object ob = getSelectedItem();
		if (ob == null)
			return Double.NaN;

		final String text = ob.toString().trim();
		if (text.equals(""))
			return Double.NaN;
		return kernel.getAlgebraProcessor().evaluateToDouble(text);
	}

}
