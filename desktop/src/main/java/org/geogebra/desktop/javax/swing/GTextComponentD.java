package org.geogebra.desktop.javax.swing;

/**
 * Wrapper for javax.swing.JTextComponent
 * 
 * @author Judit Elias
 */
public class GTextComponentD extends org.geogebra.common.javax.swing.GTextComponent {

	private javax.swing.text.JTextComponent impl = null;

	private GTextComponentD(javax.swing.text.JTextComponent impl) {
		this.impl = impl;
	}

	@Override
	public void replaceSelection(String string) {
		impl.replaceSelection(string);
	}

	/**
	 * Wraps given component
	 * 
	 * @param textField
	 *            text component to be wrapped
	 * @return wrapped text component
	 */
	public static GTextComponentD wrap(javax.swing.text.JTextComponent textField) {
		return new GTextComponentD(textField);
	}

	/**
	 * @return unwrapped text component
	 */
	public javax.swing.text.JTextComponent getImpl() {
		return this.impl;
	}

}
