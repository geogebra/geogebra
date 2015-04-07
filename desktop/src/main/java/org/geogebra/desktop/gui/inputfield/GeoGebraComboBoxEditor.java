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

	private MyTextField tf;

	/**
	 * Constructor without a specified column width.
	 * 
	 * @param app
	 */
	public GeoGebraComboBoxEditor(AppD app) {
		super();
		tf = new MyTextField(app);
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
		tf = new MyTextField(app, columns);
		tf.setShowSymbolTableIcon(true);
		tf.enableColoring(false);
	}

	public void addActionListener(ActionListener actionListener) {
		tf.addActionListener(actionListener);
	}

	public Component getEditorComponent() {
		return tf;
	}

	public Object getItem() {
		return tf.getText();
	}

	public void removeActionListener(ActionListener actionListener) {
		tf.removeActionListener(actionListener);
	}

	public void selectAll() {
		tf.selectAll();
	}

	public void setItem(Object obj) {
		if (obj != null) {
			tf.setText(obj.toString());
		} else {
			tf.setText("");
		}
	}

}