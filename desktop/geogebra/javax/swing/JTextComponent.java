package geogebra.javax.swing;

/**
 * Wrapper for javax.swing.JTextComponent
 * @author Judit Elias
 */
public class JTextComponent extends geogebra.common.javax.swing.JTextComponent {
	
	private javax.swing.text.JTextComponent impl = null; 
	
	private JTextComponent(javax.swing.text.JTextComponent impl) {
		this.impl = impl;
	}
	
	@Override
	public void replaceSelection(String string) {
		impl.replaceSelection(string);
	}

	/**
	 * Wraps given component
	 * @param textField text component to be wrapped
	 * @return wrapped text component
	 */
	public static JTextComponent wrap(javax.swing.text.JTextComponent textField) {
		return new JTextComponent(textField);
	}
	
	/**
	 * @return unwrapped text component
	 */
	public javax.swing.text.JTextComponent getImpl() {
		return this.impl;
	}

}
