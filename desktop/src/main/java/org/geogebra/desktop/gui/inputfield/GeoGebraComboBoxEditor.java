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

	private MyTextFieldD tf;

	/**
	 * Constructor without a specified column width.
	 * 
	 * @param app
	 */
	public GeoGebraComboBoxEditor(AppD app) {
		super();
		tf = new MyTextFieldD(app);
		tf.setShowSymbolTableIcon(true);
		tf.enableColoring(false);
	}

	/**
	 * Constructor with a specified column width.
	 * 
	 * @param app
	 * @param columns
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