package geogebra.javax.swing;

import geogebra.gui.inputfield.AutoCompleteTextField;

public class JTextComponent extends geogebra.common.javax.swing.JTextComponent {
	
	public javax.swing.text.JTextComponent impl = null; 
	
	private JTextComponent(javax.swing.text.JTextComponent impl) {
		this.impl = impl;
	}
	
	@Override
	public void replaceSelection(String string) {
		impl.replaceSelection(string);
	}

	public static JTextComponent wrap(javax.swing.text.JTextComponent textField) {
		return new JTextComponent(textField);
	}
	
	public javax.swing.text.JTextComponent getImpl() {
		return this.impl;
	}

}
