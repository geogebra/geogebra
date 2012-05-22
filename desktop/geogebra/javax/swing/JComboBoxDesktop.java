package geogebra.javax.swing;

import java.awt.Component;

import geogebra.common.awt.Color;
import geogebra.common.awt.Dimension;
import geogebra.common.awt.Font;
import geogebra.common.awt.Rectangle;
import geogebra.common.euclidian.event.ActionListener;
import geogebra.common.gui.inputfield.AutoCompleteTextField;
import geogebra.common.javax.swing.AbstractJComboBox;
import geogebra.common.main.AbstractApplication;

/**
 * Wrapper for javax.swing.Box
 * @author Judit Elias, Michael
 */
public class JComboBoxDesktop extends geogebra.common.javax.swing.AbstractJComboBox {
	
	private javax.swing.JComboBox impl = null; 
	
	int selectedIndex = -1;
	
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
		//if (selectedIndex < impl.getItemCount()) 
		
		impl.setSelectedIndex(selectedIndex);
		
		
	}

	@Override
	public int getSelectedIndex() {
		return impl.getSelectedIndex();
	}


	public static Component getJComboBox(AbstractJComboBox comboBox) {
		if(!(comboBox instanceof JComboBoxDesktop))
			return null;
		return ((JComboBoxDesktop)comboBox).impl;
	}

	@Override
	public void addActionListener(ActionListener newActionListener) {
		impl.addActionListener((geogebra.euclidian.event.ActionListener) newActionListener);
	}

	@Override
	public void removeAllItems() {
		impl.removeAllItems();
	}

	@Override
	public int getItemCount() {
		return impl.getItemCount();
	}		

}
