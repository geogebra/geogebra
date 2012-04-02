package geogebra.javax.swing;

import geogebra.common.awt.Color;
import geogebra.common.awt.Font;
/**
 * Wrapper for javax.swing.JLabel
 * @author Judit Elias
 */
public class JLabel extends geogebra.common.javax.swing.JLabel{
	private javax.swing.JLabel impl;

	/**
	 * Creates label wrapper 
	 * @param string text of the label
	 */
	public JLabel(String string) {
		impl = new javax.swing.JLabel(string);
	}

	/**
	 * Creates new label wrapper 
	 */
	public JLabel() {
		impl = new javax.swing.JLabel();
		
	}

	/**
	 * @return unwrapped label
	 */
	public javax.swing.JLabel getImpl(){
		return impl;
	}

	@Override
	public void setVisible(boolean b) {
		impl.setVisible(b);
		
	}

	@Override
	public void setText(String string) {
		impl.setText(string);
		
	}

	@Override
	public void setOpaque(boolean b) {
		impl.setOpaque(b);
		
	}

	@Override
	public void setFont(Font font) {
		impl.setFont(geogebra.awt.Font.getAwtFont(font));
		
	}

	@Override
	public void setForeground(Color color) {
		impl.setForeground(geogebra.awt.Color.getAwtColor(color));
		
	}

	@Override
	public void setBackground(Color color) {
		impl.setBackground(geogebra.awt.Color.getAwtColor(color));
		
	}
}
