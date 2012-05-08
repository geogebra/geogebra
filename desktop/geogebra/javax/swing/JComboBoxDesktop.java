package geogebra.javax.swing;

import java.awt.Component;

import geogebra.common.awt.Color;
import geogebra.common.awt.Dimension;
import geogebra.common.awt.Font;
import geogebra.common.awt.Rectangle;
import geogebra.common.gui.inputfield.AutoCompleteTextField;
import geogebra.common.javax.swing.AbstractJComboBox;

/**
 * Wrapper for javax.swing.Box
 * @author Judit Elias, Michael
 */
public class JComboBoxDesktop extends geogebra.common.javax.swing.AbstractJComboBox {
	
	private javax.swing.JComboBox impl = null; 
	
	/**
	 * Creates new wrapper Box
	 */
	public JComboBoxDesktop() {
		this.impl = new javax.swing.JComboBox();
	}

	@Override
	public void setVisible(boolean b) {
		impl.setVisible(b);
		
	}

	@Override
	public Object getItemAt(int i) {
		return impl.getItemAt(i);
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

	@Override
	public void setFocusable(boolean b) {
		impl.setFocusable(b);		
	}

	@Override
	public void setEditable(boolean b) {
		impl.setEditable(b);
		
	}

	@Override
	public void addItem(String string) {
		impl.addItem(string);
		
	}

	@Override
	public void setSelectedIndex(int selectedIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getSelectedIndex() {
		// TODO Auto-generated method stub
		return 0;
	}


	public static Component getJComboBox(AbstractJComboBox comboBox) {
		if(!(comboBox instanceof JComboBoxDesktop))
			return null;
		return ((JComboBoxDesktop)comboBox).impl;
	}
		

}
