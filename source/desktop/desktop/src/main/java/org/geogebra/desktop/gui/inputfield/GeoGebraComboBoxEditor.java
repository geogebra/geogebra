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

package org.geogebra.desktop.gui.inputfield;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxEditor;

import org.geogebra.desktop.main.AppD;

/**
 * ComboBoxEditor with a symbol table popup button.
 * 
 * @author G. Sturr
 * 
 */
public class GeoGebraComboBoxEditor implements ComboBoxEditor {

	private final MyTextFieldD tf;

	/**
	 * Constructor with a specified column width.
	 * 
	 * @param app application
	 * @param columns width
	 */
	public GeoGebraComboBoxEditor(AppD app, int columns) {
		super();
		tf = new MyTextFieldD(app, columns);
		tf.setShowSymbolTableIcon(true);
		tf.enableColoring(false);
	}

	@Override
	public void addActionListener(ActionListener actionListener) {
		tf.addActionListener(actionListener);
	}

	@Override
	public Component getEditorComponent() {
		return tf;
	}

	@Override
	public Object getItem() {
		return tf.getText();
	}

	@Override
	public void removeActionListener(ActionListener actionListener) {
		tf.removeActionListener(actionListener);
	}

	@Override
	public void selectAll() {
		tf.selectAll();
	}

	@Override
	public void setItem(Object obj) {
		if (obj != null) {
			tf.setText(obj.toString());
		} else {
			tf.setText("");
		}
	}

}